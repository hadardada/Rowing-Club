const refreshRate = 3000; //milliseconds
let numOfNewNoties=0;
const newMsgCountEl = document.querySelector('#newMsgCount');
const newMsgViewButton =  document.querySelector('#viewNew');
const clubMsgViewButton = document.querySelector('#clubMsg');
const sendNotificationButtonEl = document.querySelector('#postMsg');
const notificationsDivEl = document.querySelector('#notificationsDiv');

window.addEventListener('load', () => {
    setManagerSentButton ();
    //The users list is refreshed automatically every 3 seconds
    setInterval(fetchNotificationsCounter, refreshRate);
});

newMsgViewButton.addEventListener('click', getNotifications);
clubMsgViewButton.addEventListener('click', getNotifications);
sendNotificationButtonEl.addEventListener('click',postNotification);

async function setManagerSentButton (){
    const response = await fetch ("/isUserManager", {
        method: "get",
    })
    const isManager = await response.text();
    if (isManager === "true"){
        sendNotificationButtonEl.style.display = "inline-block";
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
}
async function fetchNotificationsCounter(){
    const response = await fetch ("/notifications?content=false", {
        method: "get",
    })
    const number = await response.text();
    numOfNewNoties = number;
    newMsgCountEl.textContent = " "+numOfNewNoties+" ";

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
    let notifications;
    if (newMsges) // new notifications -  get notifications as a list of strings
        notifications = await response.json();
    else //manual notifications - one string
        notifications = await response.text();

    showNotificationsAlert(notifications, newMsges);
}

function showNotificationsAlert(notifications, isNewMsges){
    if (isNewMsges) { // new notifications are each shown in a separate alert
        for (let i = 0; i < notifications.length; i++) {
            alert(notifications[i]);
        }
    }
    else { // club (manual) notifications are shown is the same alert window
        alert(notifications);
    }
}