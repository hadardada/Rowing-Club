const guestListContainerEl = document.querySelector('.activitiesList');
/* *****************************************************************
Update the DOM
******************************************************************** */
function createActivityList(activityList) {
    guestListContainerEl.innerHTML = '';

    // Create Elements on from data
    activityList.forEach((activity) => {
        const activityEl = createActivityElement(activity);
        guestListContainerEl.append(activityEl);
    });
}

function createActivityElement(activity) {
    const el = document.createElement("li");
    const nameEl = document.createElement('span');
    nameEl.innerText = activity.activityName;
    el.append(nameEl);

    const startEl = document.createElement('span');
    startEl.innerText = activity.startTime;
    el.append(startEl);

    const endEl = document.createElement('span');
    endEl.innerText = activity.endTime;
    el.append(endEl);

    if (!(activity.boatName === '')){
        const boatNameEl = document.createElement('span');
        boatNameEl.innerText = activity.boatName;
        el.append(boatNameEl);
    }
    return el;
}

/* *****************************************************************
Get Data from the server using either Promises or Async / Await
******************************************************************** */

// Async/Await API
async function refreshListUsesAsyncAwait() {
    const response = await fetch('activity/showAll');
    const value = await response.json();
    createActivityList(value);
}