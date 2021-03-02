const guestListContainerEl = document.querySelector('.activitiesList');
const deleteButtonsElements = document.querySelector('.deleteButtons');
const titlesEl = document.querySelector('#titles');
let counter = 1;


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

async function deleteActivity(){
    let id = this.id;
    const response = await fetch('/activity/delete', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(id)
    });
    counter = 1;
    refreshListUsesAsyncAwait()
}

async function editActivity(){
    let activityId = this.id.substring('edit'.length)
    window.location.href = '/activity/edit.html?id='+activityId;
}
function createActivityElement(activity) {

    const el = document.createElement("p");

    //add action button to each element
    const deleteAction = document.createElement('button');
    deleteAction.innerText = 'delete'
    deleteAction.className = 'deleteButtons';
    deleteAction.id = activity.id;
    deleteAction.addEventListener('click', deleteActivity);

    deleteAction.style.position = 'absolute';
    deleteAction.style.left = '5px'
    el.append(deleteAction);

    const editAction = document.createElement('button');
    editAction.innerText = 'edit'
    editAction.style.position = 'absolute';
    editAction.style.left = '57px';
    editAction.id = "edit"+activity.id;
    editAction.addEventListener('click', editActivity);
    el.append(editAction);

    const number = document.createElement("span")
    number.innerText = " "+counter.toString()+'.';
    el.append(number);
    counter++;
    number.style.position = 'absolute';
    number.style.left = '115px'

    const nameEl = document.createElement('span');
    nameEl.innerText = activity.activityName;
    el.append(nameEl);
    nameEl.style.position = 'absolute';
    nameEl.style.left = '150px'

    const startEl = document.createElement('span');
    startEl.innerText = activity.startTime;
    el.appendChild(startEl)
    startEl.style.position = 'absolute';
    startEl.style.left = '250px'

    const endEl = document.createElement('span');
    endEl.innerText = activity.endTime;
    el.appendChild(endEl)
    endEl.style.position = 'absolute';
    endEl.style.left = '340px'

    if (!(activity.boatName === '')){
        const boatNameEl = document.createElement('span');
        boatNameEl.innerText = activity.boatName;
        el.appendChild(boatNameEl)
        boatNameEl.style.position = 'absolute';
        boatNameEl.style.left = '450px'
    }
    return el;
}

function createActivityList(activityList) {
    guestListContainerEl.innerHTML = '';
    const nameTitle = document.createElement("span");
    nameTitle.innerText = ' NAME '
    const fromeTitle = document.createElement("span");
    fromeTitle.innerText = ' FROM '
    const toTitle = document.createElement("span");
    toTitle.innerText = ' TO '
    const boatTitle = document.createElement("span");
    boatTitle.innerText = ' BOAT TYPE (OPTIONAL) '
    nameTitle.append(fromeTitle,toTitle,boatTitle);
    titlesEl.append(nameTitle);
    nameTitle.style.position = 'absolute';
    nameTitle.style.left = '150px'
    nameTitle.style.textDecoration = "underline";
    fromeTitle.style.position = 'absolute';
    fromeTitle.style.left = '100px'
    fromeTitle.style.textDecoration = "underline";
    toTitle.style.position = 'absolute';
    toTitle.style.left = '200px'
    toTitle.style.textDecoration = "underline";
    boatTitle.style.position = 'absolute';
    boatTitle.style.left = '300px'
    boatTitle.style.textDecoration = "underline";
    guestListContainerEl.style.position = 'absolute';
    guestListContainerEl.style.top = '100px'


    // Create Elements on from data
    activityList.forEach((activity) => {
        const activityEl = createActivityElement(activity);
        guestListContainerEl.append(activityEl);
        const nameTitle = document.createElement("br");
        guestListContainerEl.append(nameTitle);
    });

    const Title = document.createElement("br");
    guestListContainerEl.append(Title);
    const backEl = document.createElement("a");
    backEl.style.position = 'absolute';
    backEl.style.left = '5px';
    backEl.style.fontSize = 'small';
    backEl.href = "/menu/activity.html";
    backEl.innerText = "Go Back";
    guestListContainerEl.append(backEl);
}