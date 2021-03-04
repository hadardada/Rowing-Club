const refreshRate = 3000; //milliseconds
const refreshList = 2000;
const NO_NOTIES = "No notifications to show!"
let numOfNewNoties=0;
const newMsgCountEl = document.querySelector('#newMsgCount');
const newMsgViewButton =  document.querySelector('#viewNew');
const clubMsgViewButton = document.querySelector('#clubMsg');
const manageButtonEl = document.querySelector('#goToManager');
const sendNotificationButtonEl = document.querySelector('#postMsg');
const deleteMsgFormEl = document.querySelector('#deleteMsgForm');
const noNotoficationMsg = document.querySelector('#isThere');

const queryStringOnNotifications = window.location.pathname;
const deletebuttonEl = document.querySelector('#deleteBut');


if (queryStringOnNotifications !== "/notifications/manage.html") { // if we are not in the managing page
    window.addEventListener('load', () => {
        fetchNotificationsCounter();
        setManagerSentButton();
        //The users list is refreshed automatically every 3 seconds
        setInterval(fetchNotificationsCounter, refreshRate);
        newMsgViewButton.addEventListener('click', getNotifications);
        clubMsgViewButton.addEventListener('click', getNotifications);
        manageButtonEl.addEventListener('click', redirectToManager);
    });
}
else {

    window.addEventListener('DOMContentLoaded',showClubList);
    deleteMsgFormEl.addEventListener('submit', ()=>{
        setTimeout(showClubList, refreshList);
    });
    sendNotificationButtonEl.addEventListener('click',postNotification);

}


async function setManagerSentButton (){
    const response = await fetch ("/isUserManager", {
        method: "get",
    })
    const isManager = await response.text();
    if (isManager === "true"){
        manageButtonEl.style.display = "inline-block";
    }
}

async function postNotification(){
    let today = new Date();
    let defaultOpenning = today.toDateString() + " update: "
    let msg = prompt("Please write your message",defaultOpenning);
    if (msg == null || msg == "") {
    } else {
        const response = await fetch ("/notifications", {
            method: "post",
            body: msg,
        })
        if (response.ok)
            alert("Message has been post to all club members");
    }

    let radioParentEl = document.querySelector('#radioParent');
    let formRadiosElements = document.querySelector('#rediosSpan');
    radioParentEl.removeChild(formRadiosElements);
    formRadiosElements = document.createElement("span");
    formRadiosElements.setAttribute("id","rediosSpan");
    radioParentEl.appendChild(formRadiosElements);

    await showClubList();

}
async function fetchNotificationsCounter(){
    const response = await fetch ("/notifications?content=false", {
        method: "get",
    })
    const number = await response.text();
    if (response.ok) {
        numOfNewNoties = number;
        newMsgCountEl.textContent = " " + numOfNewNoties + " ";
    }

}

async function getNotifications(){
    let typeOfNoti;
    let newMsges = false;
    if (this === newMsgViewButton){
        if (numOfNewNoties === 0) // no new notifications to pull
            return;
        typeOfNoti = "new";
        newMsges = true;
    }
    else
        typeOfNoti = "club";
    const response = await fetch ("/notifications?content="+typeOfNoti, {
        method: "get",
    })
    let notifications = await response.json();

    showNotificationsAlert(notifications, newMsges);
}

function redirectToManager (){
    location.replace("/notifications/manage.html");
}

function showNotificationsAlert(notifications, isNewMsges){
    if (isNewMsges) { // new notifications are each shown in a separate alert
        for (let i = 0; i < notifications.length; i++) {
            alert(notifications[i]);
        }
    }
    else { // club (manual) notifications are shown is the same alert window
        let allClubMsgs = '';
        let i = 0;
        for (i; i < notifications.length; i++) {
            allClubMsgs = allClubMsgs+notifications[i].content +"\n\r";
        }
        if (i ===0){
            alert(NO_NOTIES);
        }
        alert(allClubMsgs);
    }
}

async function showClubList (){
    const response = await fetch ("/notifications?content=club", {
        method: "get",
    })
    const notifications = await response.json();
    let i = 0;
    let formRadiosElements = document.querySelector('#rediosSpan');
    noNotoficationMsg.textContent = '';

    for (i ; i < notifications.length; i++) {
        newNotiRadioEl = document.createElement("input");
        formRadiosElements.appendChild(newNotiRadioEl);
        newNotiRadioEl.setAttribute("type", "radio");
        newNotiRadioEl.setAttribute("id", notifications[i].serialNum.toString());
        newNotiRadioEl.setAttribute("name", "notificationsRadios");
        newNotiRadioEl.setAttribute("value",  notifications[i].serialNum.toString());

        newNotiRadioLabelEl = document.createElement("label");
        formRadiosElements.appendChild(newNotiRadioLabelEl);
        newNotiRadioLabelEl.setAttribute("for",notifications[i].serialNum );
        newNotiRadioLabelEl.textContent = notifications[i].content;
        formRadiosElements.appendChild(document.createElement("br"));

    }
    if (i===1){
        newNotiRadioEl.checked = true;
        deletebuttonEl.disabled = false;

    }
    if (i ===0){
        noNotoficationMsg.textContent = NO_NOTIES;
        deletebuttonEl.disabled = true;
    }
}