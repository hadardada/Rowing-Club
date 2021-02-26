let maxResPerActivity =[];
let activitiesIdsMap = new Map();
const FULL_WEEK_DAYS = 8;
const firstRow = document.getElementsByTagName("tr")[0];
const activitiesRows = document.getElementsByTagName("tbody")[0];


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
        activitiesIdsMap.set(activities[i].id, i); // sets the key in the map so it is easire to fimd activity's row by it's id
        maxResPerActivity[i] = 1; // the max height of each activity row is set to 1
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
    //activityRow.setAttribute('id', activity.id); // id is a string
    for (let i =0; i<FULL_WEEK_DAYS; i++){
        let emptyRow =  document.createElement('td');
        activityRow.appendChild(emptyRow);
        //emptyRow.textContent = 'j';
    }
    return activityRow;
}

async function getReservationsOnDate(nextDayDate) {
    let reqDate = nextDayDate.toISOString().substring(0, 10); //now formatted as YYYY-MM-DD
    const response =  await fetch ('/reservation/weekly?date='+reqDate+'&status='+status);
    let reservations = await response.json();
    return reservations;
}

async function fillOutResOnDate(index) {
    let nextDayDate = new Date();
    nextDayDate.setDate(nextDayDate.getDate() + index-1);
    let reservations = await getReservationsOnDate(nextDayDate);
    if (reservations.length !== 0){ // it might be that there are no reservations at all for that date
        for (let i=0;i<reservations.length; i++){
            let activityRowIndx = activitiesIdsMap.get(reservations[i].activityId);
            let activityRowEl = activitiesRows.getElementsByTagName("tr")[activityRowIndx];
            let resCellEl = activityRowEl.getElementsByTagName('td')[index];
            let currMaxOfActivities = maxResPerActivity[activityRowIndx];
            for(j=0; j<=currMaxOfActivities; j++){
                    if (j === maxResPerActivity[activityRowIndx]) {
                        // meaning there are not enough rows to contain all reservation on that date with this activity
                        maxResPerActivity[activityRowIndx]++;
                        activityRowEl.getElementsByTagName('td')[0].rowSpan = maxResPerActivity[activityRowIndx];

                        let newRow = createNewRow();
                        if (activityRowIndx !== maxResPerActivity.length-1)
                            // if we are not at the last activity
                            activitiesRows.insertBefore(newRow, activityRowEl);
                        else
                            activitiesRows.insertBefore(newRow, null);
                        resCellEl = newRow.getElementsByTagName('td')[index];
                        setResContent(reservations[i], resCellEl);
                        // resCellEl.textContent = setResContent(reservations[i]);
                    }
                    else if (resCellEl.textContent === ''){
                        setResContent(reservations[i], resCellEl);
                        j = maxResPerActivity[activityRowIndx] +1; // to break the loop
                    }
                    else{
                        activityRowEl = activityRowEl.nextSibling;
                        resCellEl = activityRowEl.getElementsByTagName('td')[index];}

               }

        }
    }
}

function setResContent (reservation, resCellEl){
     let newNode = document.createElement('p');
     let newNode2 = document.createElement('p');
     let hyper
    resCellEl.appendChild(newNode);
    resCellEl.appendChild(newNode2);
    if (status === 'all') {
        newNode.textContent = "Reservation status: " + reservation.status;
        newNode2.textContent = "Main Rower: " + reservation.mainRower;
    }
    else if (status === 'approved'){
        newNode.textContent = "Reservation status: " + reservation.status;
        newNode2.textContent = "Main Rower: " + reservation.mainRower;
    }

}
//function putResOnCell (reservation, in)

function createNewRow(){
    let newRow = document.createElement('tr');
    for (i=0;i<FULL_WEEK_DAYS;i++){
        let newRowText = document.createElement('td');
        newRow.appendChild(newRowText);
    }
    return newRow;

}