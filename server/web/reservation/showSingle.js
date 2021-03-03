const statusEl = document.querySelector('#status');
const mainMemberEl = document.querySelector('#mainMember');
const dateEl = document.querySelector('#date');
const timeEl = document.querySelector('#time');
const boatTypeEl = document.querySelector('#boatType');
const wantedRowersEl = document.querySelector('#WantedRowers');
const resMadeByEl = document.querySelector('#resMadeBy');
const resMadeAtEl = document.querySelector('#resMadeAt');
const actionsEl = document.querySelector('#Actions');
const errorEl = document.querySelector('#errorSpan');



const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let createdOnId = urlParams.get('createdOn');
let creator = urlParams.get('creator');
let date = urlParams.get('date');

showSingle()

async function showSingle() {
    const response = await fetch('/reservation/showSingle?creator='+creator+'&createdOn='+createdOnId+'&date='+date, {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    const value = await response.json();
    createRes(value);
}

async function editRes()
{
    const response = await fetch('/reservation/show', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
}

async function deleteRes()
{
    window.location.href = '/reservation/delete.html?creator=' + creator + '&createdOn=' + createdOnId + '&date=' + date;
}

async function mergeRes(){
    window.location.href = '/reservation/merge.html?creator=' + creator + '&createdOn=' + createdOnId + '&date=' + date;
}

async function rejectRes(){
    window.location.href = '/reservation/reject.html?creator=' + creator + '&createdOn=' + createdOnId + '&date=' + date;
}

async function approveRes() {
    window.location.href = '/reservation/approve.html?creator=' + creator + '&createdOn=' + createdOnId + '&date=' + date;
}

async function reopenRes(){
    const response = await fetch('/reservation/reopen?creator='+creator+'&createdOn='+createdOnId+'&date='+date, {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    if (response.status === 200)
    {
        formErrorEl.textContent = "The Reservation reopened Successfully!"
        formErrorEl.style.color = "green";
    }
    else{
        formErrorEl.textContent = "ERROR! " + await response.text();
        formErrorEl.style.color = "red";
    }
}

async function copyRes(){
    const response = await fetch('/reservation/copy?creator='+creator+'&createdOn='+createdOnId+'&date='+date, {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    if (response.status === 200)
    {
        errorEl.textContent = "The Reservation Copied Successfully !"
        errorEl.style.color = "green";
    }
    else{
        errorEl.textContent = "ERROR! " + await response.text();
        errorEl.style.color = "red";
    }
}

function createRes(res) {

    mainMemberEl.innerText = res.participantRowerEmail;
    dateEl.innerText = res.trainingDate;
    timeEl.innerText = res.activityTime;
    res.boatTypes.forEach((type)=>{
        const typeEl = document.createElement("span");
        typeEl.innerText = type;
        boatTypeEl.appendChild(typeEl)
        const newLineEl = document.createElement("br");
        boatTypeEl.appendChild(newLineEl)
    });

    res.wantedMemberEmails.forEach((member)=>{
        const memberEl = document.createElement("span");
        memberEl.innerText = member;
        wantedRowersEl.appendChild(memberEl)
        const newLineEl = document.createElement("br");
        wantedRowersEl.appendChild(newLineEl)
    });

    resMadeByEl.innerText = res.reservationMadeBy;
    resMadeAtEl.innerText = res.getReservationMadeAt;

    if (res.status===2){
        statusEl.innerText = "APPROVED";
        const boatTitleEl = document.createElement('p');
        boatTitleEl.innerText = "Booked Boat:";
        boatTitleEl.style.fontWeight = 'bold';
        statusEl.append(boatTitleEl);
        const newLineEl = document.createElement("br");
        boatTitleEl.appendChild(newLineEl)
        const boat = document.createElement('span');
        boat.innerText = res.boat;
        boatTitleEl.append(boat);

        const rowersTitleEl = document.createElement('p');
        rowersTitleEl.innerText = "Participant Rowers:";
        rowersTitleEl.style.fontWeight = 'bold';
        statusEl.append(rowersTitleEl);

        res.actualMemberEmails.forEach((memberP)=>{
            const memberPEl = document.createElement("span");
            const newLineEl = document.createElement("br");
            rowersTitleEl.appendChild(newLineEl)
            memberPEl.innerText = memberP;
            rowersTitleEl.appendChild(memberPEl)
        });
    }

    if (res.status===1 && res.isManager) {
        const approveAction = document.createElement('button');
        approveAction.innerText = 'Approve'
        approveAction.style.position = 'absolute';
        approveAction.style.left = '5px'
        approveAction.style.backgroundColor = 'green'
        actionsEl.append(approveAction);
        approveAction.addEventListener('click', approveRes);

        const mergeAction = document.createElement('button');
        mergeAction.innerText = 'Merge'
        mergeAction.style.position = 'absolute';
        mergeAction.style.left = '70px'
        actionsEl.append(mergeAction);
        mergeAction.addEventListener('click', mergeRes);

        const rejectAction = document.createElement('button');
        rejectAction.innerText = 'Reject'
        rejectAction.style.position = 'absolute';
        rejectAction.style.left = '265px'
        actionsEl.append(rejectAction);
        rejectAction.addEventListener('click', rejectRes);
    }
    if (res.status===1) {
        statusEl.innerText = "PENDING";
        const editAction = document.createElement('button');
        editAction.innerText = 'Edit'
        editAction.style.position = 'absolute';
        editAction.style.left = '124px'
        actionsEl.append(editAction);
        editAction.addEventListener('click', editRes);

    if (!(res.isManager)) {
        editAction.style.left = '5px'
        const deleteAction = document.createElement('button');
        deleteAction.innerText = 'Delete'
        deleteAction.className = 'deleteButtons';
        deleteAction.addEventListener('click', deleteRes);
        deleteAction.style.position = 'absolute';
        deleteAction.style.left = '60px'
        actionsEl.append(deleteAction);
    }
    }

    if ((res.status === 1 || res.status === 2) && res.isManager) {
        const copyAction = document.createElement('button');
        copyAction.innerText = 'Copy'
        copyAction.style.position = 'absolute';
        copyAction.style.left = '163px'
        actionsEl.append(copyAction);
        copyAction.addEventListener('click', copyRes);

        const deleteAction = document.createElement('button');
        deleteAction.innerText = 'Delete'
        deleteAction.className = 'deleteButtons';
        deleteAction.addEventListener('click', deleteRes);
        deleteAction.style.position = 'absolute';
        deleteAction.style.left = '210px'
        actionsEl.append(deleteAction);

        if (res.status === 2 && res.isManager) {
            const reopenAction = document.createElement('button');
            reopenAction.innerText = 'Reopen'
            reopenAction.style.position = 'absolute';
            reopenAction.style.left = '5px'
            actionsEl.append(reopenAction);
            reopenAction.addEventListener('click', reopenRes);

            copyAction.style.left = '68px'
            deleteAction.style.left = '115px'
        }
    }
    if (res.status === 3){
        statusEl.innerText = "REJECTED";
    }
}