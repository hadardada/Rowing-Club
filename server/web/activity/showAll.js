const guestListContainerEl = document.querySelector('.activitiesList');
const deleteButtonsElements = document.querySelector('.deleteButtons');


refreshListUsesAsyncAwait()

async function refreshListUsesAsyncAwait() {
    const response = await fetch('/activity/showAll', {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    const value = await response.json();
    createActivityList(value);
}

async function deleteActivity(id){
    const response = await fetch('/activity/delete', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(id)
    });
}

function createActivityElement(activity) {
    const el = document.createElement("li");

    //add action button to each element
    const deleteAction = document.createElement('button');
    deleteAction.innerText = 'delete'
    deleteAction.className = 'deleteButtons';
    deleteAction.id = 'deleteButtons' + activity.id;
    deleteAction.onclick = deleteActivity(activity.id);
   // deleteAction.addEventListener('click', deleteActivity(activity.id))
    el.append(deleteAction);

    const editAction = document.createElement('button');
    editAction.innerText = 'edit'
    el.append(editAction);

    const nameEl = document.createElement('span');
    nameEl.innerText = activity.activityName;
    el.append(nameEl);

    const startEl = document.createElement('span');
    startEl.innerText = activity.startTime;
    el.appendChild(startEl)

    const endEl = document.createElement('span');
    endEl.innerText = activity.endTime;
    el.appendChild(endEl)

    if (!(activity.boatName === '')){
        const boatNameEl = document.createElement('span');
        boatNameEl.innerText = activity.boatName;
        el.appendChild(boatNameEl)
    }
    return el;
}

function createActivityList(activityList) {
    guestListContainerEl.innerHTML = '';

    // Create Elements on from data
    activityList.forEach((activity) => {
        const activityEl = createActivityElement(activity);
        guestListContainerEl.append(activityEl);
    });
}




