let maxResPerActivity =new Map();
let activitiesIdsMap = new Map();
const FULL_WEEK_DAYS = 8;
const firstRow = document.getElementsByTagName("tr")[0];
const activitiesRows = document.getElementsByTagName("tbody")[0];
const infoMsgEl = document.querySelector('#pageInfo');

function shortReservation (){
    createdOn;
    createdBy;
    mainRower;
    activityId;
    boatId;
    status;
}

//get activity required status reservation (scheduling / all)
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const status = urlParams.get('status');

if (status ==="all")
    infoMsgEl.textContent = "Displaying all reservations for this week:";
else if (status === "approved")
    infoMsgEl.textContent = "Displaying all approved reservations for this week (AKA: This Week's Scheduling):";



window.addEventListener('DOMContentLoaded', createTable);

 async function createTable () {
    let today = new Date();
    let activitiesCul =document.createElement('th');
    activitiesCul.textContent = '';
    firstRow.appendChild(activitiesCul);

    //build weeks dates row (first row of the table)
    for (let i = 0; i < FULL_WEEK_DAYS; i++) {
        let nextDay = document.createElement('th');
        let nextDayDate = new Date();
        nextDayDate.setDate(today.getDate() + i);
        nextDay.textContent = nextDayDate.getDate() + '.' + (nextDayDate.getMonth() + 1);
        firstRow.appendChild(nextDay);
    }
    //build activities column
    const activities = await getActivities();
    const numOfActivities = activities.length;
    maxResPerActivity.length = numOfActivities;
    for (let i=0;i<numOfActivities;i++){
        const activityEl = createActivityElement(activities[i]);
        activitiesRows.appendChild(activityEl);
        activitiesIdsMap.set(activities[i].id, i+1); // sets the key in the map so it is easire to fimd activity's row by it's id
        maxResPerActivity.set (activities[i].id, 1); // the max height of each activity row is set to 1
    }


    //fill out the table with reservations data
    for (let i = 1; i <= FULL_WEEK_DAYS; i++) {
        await fillOutResOnDate(i);
    }
}

async function getActivities(){
    const response = await fetch ('/activity/showAll',
        {
            method: 'get',
            headers: new Headers({
            }),
           // body: status
        });

    const activities = await response.json();
    return activities;

}

function createActivityElement(activity){
    let activityRow = document.createElement('tr');
    let activityRowText = document.createElement('td');
    activityRow.appendChild(activityRowText);
    let firstLine = document.createElement('p');
    let secLine = document.createElement('p')
    activityRowText.appendChild(firstLine);
    activityRowText.appendChild(secLine);
    firstLine.textContent = activity.activityName;
    secLine.textContent = activity.startTime+ " - " + activity.endTime;
    activityRow.setAttribute('id', "act"+activity.id); // id is a string
    for (let i =0; i<FULL_WEEK_DAYS; i++){
        let emptyRow =  document.createElement('td');
        activityRow.appendChild(emptyRow);
        //emptyRow.textContent = 'j';
    }
    return activityRow;
}

async function getReservationsOnDate(reqDate) {
    const response =  await fetch ('/reservation/weekly?date='+reqDate+'&status='+status);
    let reservations = await response.json();
    return reservations;
}

async function fillOutResOnDate(index) {
    let nextDayDate = new Date();
    nextDayDate.setDate(nextDayDate.getDate() + index-1);
    let reqDate = nextDayDate.toISOString().substring(0, 10); //now formatted as YYYY-MM-DD
    let reservations = await getReservationsOnDate(reqDate);
    if (reservations.length !== 0){ // it might be that there are no reservations at all for that date
        for (let i=0;i<reservations.length; i++){
            let activityRowIndx = activitiesIdsMap.get(reservations[i].activityId);
            let activityRowEl = document.getElementsByTagName('tr')[activityRowIndx];
            let currRow = document.getElementsByTagName('tr')[activityRowIndx];
            //let rowId = '#act'+reservations[i].activityId;
            //let activityRowEl =document.getElementById(rowId);
            let resCellEl = currRow.getElementsByTagName('td')[index];
            let currMaxOfActivities = maxResPerActivity.get(reservations[i].activityId);
            for(j=0; j<=currMaxOfActivities; j++){
                    if (j === maxResPerActivity.get(reservations[i].activityId)) {
                        // meaning there are not enough rows to contain all reservation on that date with this activity
                        maxResPerActivity.set(reservations[i].activityId, maxResPerActivity.get(reservations[i].activityId)+1);
                        updateIndexMap(activityRowIndx,maxResPerActivity.length );
                        activityRowEl.getElementsByTagName('td')[0].rowSpan =maxResPerActivity.get(reservations[i].activityId);
                        let newRow = createNewRow();
                        if (activityRowIndx !== maxResPerActivity.length-1) {
                            // if we are not at the last activity
                            //activityRowEl = activitiesRows.getElementsByTagName("tr")[activityRowIndx+1];
                            activitiesRows.insertBefore(newRow, currRow);
                        }
                        else
                            activitiesRows.insertBefore(newRow, null);
                        resCellEl = newRow.getElementsByTagName('td')[index-1];
                        setResContent(reservations[i], resCellEl, reqDate);
                        // resCellEl.textContent = setResContent(reservations[i]);
                    }
                    else if (resCellEl.textContent === ''){
                        setResContent(reservations[i], resCellEl, reqDate);
                        j = maxResPerActivity.get(reservations[i].activityId) +1; // to break the loop
                    }
                    else {
                        currRow = currRow.nextSibling;
                        //if(maxResPerActivity[activityRowIndx]>1)
                        resCellEl = currRow.getElementsByTagName('td')[index - 1];
                        //else
                        //resCellEl = currRow.getElementsByTagName('td')[index];}
                    }
               }

        }
    }
}

function setResContent (reservation, resCellEl, resDate){
     let newNode = document.createElement('p');
     let newNode2 = document.createElement('p');
     let newNode3 = document.createElement('p');
     let resRef = document.createElement('a');
     let url = new URLSearchParams({
         "creator": reservation.createdBy,
         "createdOn": reservation.createdOn,
         "date":resDate,
     });
        let link = "/reservation/showSingle.html?";
    // link = link.concat("&createdOn="+ reservation.createdOn);
    // link = link.concat("&date="+resDate);
    resRef.href = link+url;
    resRef.textContent = "Manage Reservation"
    newNode3.appendChild(resRef);
    resCellEl.appendChild(newNode);
    resCellEl.appendChild(newNode2);
    resCellEl.appendChild(newNode3);
    if (status === 'all') {
            newNode.textContent = "Status: ";
            let statusSpan = document.createElement('span');
            newNode.appendChild(statusSpan);
            if (reservation.status === "Pending")
                statusSpan.style.color="red";
            else if (reservation.status === "Approved")
                statusSpan.style.color="green";
            statusSpan.textContent = reservation.status;
            newNode2.textContent = "Main Rower: " + reservation.mainRower;
    }
    else if (status === 'approved'){
        newNode.textContent = "Assigned Boat's id: " + reservation.boatId;
        newNode2.textContent = "Main Rower: " + reservation.mainRower;
    }

}
//function putResOnCell (reservation, in)

function createNewRow(){
    let newRow = document.createElement('tr');
    for (let i=0;i<FULL_WEEK_DAYS;i++){
        let newRowText = document.createElement('td');
        newRow.appendChild(newRowText);
    }
    return newRow;

 }

 function updateIndexMap (fromIndx, numOfActivities){
     for (let i=fromIndx+1; i<=numOfActivities;i++)
        activitiesIdsMap.set(i.toString(), (activitiesIdsMap.get(i.toString())+1));
 }