let maxResPerActivity;
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

 function createTable () {
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
    const activities = getActivities();
    const numOfActivities = activities.length;
    for (let i=0;i<numOfActivities;i++){
        const activityEl = createActivityElement(activities[i]);
        activitiesRows.appendChild(activityEl);
        activitiesIdsMap.set(activities[i].id, i);
    }


    //fill out the table with reservations data
    for (let i = 0; i < FULL_WEEK_DAYS; i++) {

        fillOutResOnDate(i);
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

    return await response.json();

}

function createActivityElement(activity){
    let activityRow = document.createElement('tr');
    let activityRowText = document.createElement('td');
    activityRow.appendChild(activityRowText);
    activityRowText.textContent = activity.activityName +" "+activity.startTime+ " - " + activity.endTime;
    activityRow.setAttribute('id', activity.id); // id is a string
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

function fillOutResOnDate(index) {
    let nextDayDate = new Date();
    nextDayDate.setDate(nextDayDate.getDate() + index);
    let reservations = getReservationsOnDate(nextDayDate);
    if (reservations.length !== 0){ // it might be that there are no reservations at all for that date
        for (let i=0;i<reservations.length; i++){
            let activityRowEl = document.querySelector('#'+reservations[i].id);
            let requstedCell = activityRowEl.getElementsByTagName('td')[0];
            while (requstedCell !== ''){
                requstedCell = requstedCell.nextSibling;
            }

        }
    }
}

//function putResOnCell (reservation, in)

