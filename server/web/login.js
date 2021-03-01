window.addEventListener('DOMContentLoaded',showErrorFromServer);
const userNameErrEl = document.querySelector("#usernameError");
const passwordErrEl = document.querySelector("#passwordError");
const userNameFieldEl = document.querySelector('#username');

async function showErrorFromServer(){
    const response = await fetch('/loginToServer', {
        method: 'get'})
    const errMsgObject = await response.json();
    let userNameStr = errMsgObject.sentUserName;
    showError(errMsgObject);

}


function showError(errMsgObject){
    userNameErrEl.textContent = '';
    passwordErrEl.textContent='';
    if (errMsgObject.errorMsg === "usernameErr"){
        let userNameErrorContent = "No Such Member: \""+ errMsgObject.sentUserName;
        userNameErrorContent = userNameErrorContent + "\" in the club. Please type a username of an exiting member";
        userNameErrEl.textContent  = userNameErrorContent;
    }
    else if (errMsgObject.errorMsg === "passwordErr") {
        passwordErrEl.textContent = "Wrong Password! Please try again";
        userNameFieldEl.value =errMsgObject.sentUserName;
    }
}