let maxResPerActivity =new Map();
let activitiesIdsMap = new Map();
const FULL_WEEK_DAYS = 8;
const firstRow = document.getElementsByTagName("tr")[0];
const activitiesRows = document.getElementsByTagName("tbody")[0];
let infoMsgEl = document.querySelector('#pageInfo');

function shortReservation (){
    createdOn;
    createdBy;
    mainRower;
    activityId;
    boatId;
    status;
}

//get required status reservation (scheduling / all)
const queryStringWeekly = window.location.search;
const urlParams = new URLSearchParams(queryStringWeekly);
const status = urlParams.get('status');
let date = urlParams.get('date');
let dateIndex;
//check if we are intersted with shoing only one day ore the whole week
let weekly;
if (date === null)
    weekly = true;
else {
    weekly = false;
}

if (weekly) {
    if (status === "all")
        infoMsgEl.textContent = "Displaying all reservations for this week:";
    else if (status === "approved")
        infoMsgEl.textContent = "Displaying all approved reservations for this week (AKA: This Week's Scheduling):";
}
else{
    if (status ==="all")
        infoMsgEl.textContent = "Displaying all reservations on "+date;
    else if (status === "approved")
        infoMsgEl.textContent = "Displaying all approved reservations on"+date+ "(AKA: This Week's Scheduling):";

}



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
            if (!weekly){// if we are interested in showing only one day
                if (nextDayDate.toISOString().substring(0,10) ===date){
                    dateIndex = i+1; // keeps the index of the query date
                    nextDay.textContent = nextDayDate.getDate() + '.' + (nextDayDate.getMonth() + 1);
                    firstRow.appendChild(nextDay);
                    nextDay.textContent = nextDayDate.getDate() + '.' + (nextDayDate.getMonth() + 1);
                    firstRow.appendChild(nextDay);
                    i=FULL_WEEK_DAYS;

                }
            }
            else{
                nextDay.textContent = nextDayDate.getDate() + '.' + (nextDayDate.getMonth() + 1);
                firstRow.appendChild(nextDay);
            }
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


    if (weekly){
        //fill out the table with reservations data
        for (let i = 1; i <= FULL_WEEK_DAYS; i++) {
            await fillOutResOnDate(i);
        }
    }
    else
        await fillOutResOnDate(dateIndex);

 }

async function getActivities(){
    const response = await fetch ('/boathouse/activity/showAll',
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
        if (!weekly)
            i = FULL_WEEK_DAYS;
    }
    return activityRow;
}

async function getReservationsOnDate(reqDate) {
    const response =  await fetch ('/boathouse/reservation/weekly?date='+reqDate+'&status='+status);
    let reservations = await response.json();
    return reservations;
}
//this functions fills out the columns of the dates by goings over 8 days loop and pullin the data for
//each day from server
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
                        if (activityRowIndx !== maxResPerActivity.length) {
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
                        if(currRow!= null)
                            resCellEl = currRow.getElementsByTagName('td')[index - 1];
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
     let link = "/boathouse/reservation/showSingle.html?creator="+reservation.createdBy;
     link = link + "&createdOn="+ reservation.createdOn;
     link = link + "&date="+resDate;
    resRef.href = link;
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
        if (!weekly)
            i = FULL_WEEK_DAYS;
    }
    return newRow;

 }

 function updateIndexMap (fromIndx, numOfActivities){
     for (let i=fromIndx+1; i<=numOfActivities;i++)
        activitiesIdsMap.set(i.toString(), (activitiesIdsMap.get(i.toString())+1));
 }