const statusEl = document.querySelector('#status');
const mainMemberEl = document.querySelector('#mainMember');
const dateEl = document.querySelector('#date');
const timeEl = document.querySelector('#time');
const boatTypeEl = document.querySelector('#boatType');
const wantedRowersEl = document.querySelector('#WantedRowers');
const resMadeByEl = document.querySelector('#resMadeBy');
const resMadeAtEl = document.querySelector('#resMadeAt');
const actionsEl = document.querySelector('#Actions');

const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let createdOnId = urlParams.get('createdOn');
let creator = urlParams.get('creator');
let date = urlParams.get('date');

showSingle()

async function showSingle() {
    const response = await fetch('/reservation/showSingle?creator='+creator+'createdOn='+createdOnId+'date='+date, {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    const value = await response.json();
    createRes(value);
}

function createRes(res) {

    statusEl.innerText = res.status;
    mainMemberEl.innerText = res.participantRowerEmail;
    dateEl.innerText = res.trainingDate;
    timeEl.innerText = res.activityTime;
    res.boatTypes.forEach((type)=>{
        const typeEl = document.createElement("span");
        typeEl.innerText = type;
        boatTypeEl.appendChild(typeEl)
    });

    res.wantedMemberEmails.forEach((member)=>{
        const memberEl = document.createElement("span");
        memberEl.innerText = member;
        wantedRowersEl.appendChild(memberEl)
    });

    resMadeByEl.innerText = res.reservationMadeBy;
    resMadeAtEl.innerText = res.getReservationMadeAt;

    if (res.status==='1'){
        const boatTitleEl = document.createElement('p');
        boatTitleEl.innerText = "Booked Boat:";
        statusEl.append(boatTitleEl);
        const boat = document.createElement('span');
        boat.innerText = res.boat;
        boatTitleEl.append(boat);

        const rowersTitleEl = document.createElement('p');
        rowersTitleEl.innerText = "Participant Rowers:";
        statusEl.append(rowersTitleEl);

        res.actualMemberEmails.forEach((memberP)=>{
            const memberPEl = document.createElement("span");
            memberPEl.innerText = memberP;
            wantedRowersEl.appendChild(memberPEl)
        });
    }

    if (res.status==='2'){
        const approveAction = document.createElement('button');
        approveAction.innerText = 'Approve'
        approveAction.style.position = 'absolute';
        approveAction.style.left = '5px'
        actionsEl.append(approveAction);
        approveAction.addEventListener('click', approveRes);

        const mergeAction = document.createElement('button');
        mergeAction.innerText = 'Merge'
        mergeAction.style.position = 'absolute';
        mergeAction.style.left = '60px'
        actionsEl.append(mergeAction);
        mergeAction.addEventListener('click', mergeRes);

    }
    const editAction = document.createElement('button');
    editAction.innerText = 'Edit'
    editAction.style.position = 'absolute';
    editAction.style.left = '57px'
    actionsEl.append(editAction);
    editAction.addEventListener('click', editRes);

    const copyAction = document.createElement('button');
    copyAction.innerText = 'Copy'
    copyAction.style.position = 'absolute';
    copyAction.style.left = '57px'
    actionsEl.append(copyAction);
    deleteAction.addEventListener('click', copyRes);

    const deleteAction = document.createElement('button');
    deleteAction.innerText = 'Delete'
    deleteAction.className = 'deleteButtons';
    deleteAction.id = activity.id;
    deleteAction.addEventListener('click', deleteRes);
    deleteAction.style.position = 'absolute';
    deleteAction.style.left = '5px'
    actionsEl.append(deleteAction);

    if (res.status==='2'){
        const reopenAction = document.createElement('button');
        reopenAction.innerText = 'Re-open'
        reopenAction.style.position = 'absolute';
        reopenAction.style.left = '57px'
        actionsEl.append(reopenAction);
        deleteAction.addEventListener('click', reopenRes);
    }
}