window.addEventListener('DOMContentLoaded',showErrorFromServer);
const userNameErrEl = document.querySelector("#usernameError");
const passwordErrEl = document.querySelector("#passwordError");

async function showErrorFromServer(){
    const response = await fetch('/loginToServer', {
        method: 'get'})
    const result = await response.text();
    showError(result);

}

function showError(errMsg){
    userNameErrEl.textContent = '';
    passwordErrEl.textContent='';
    if (errMsg === "usernameErr")
        userNameErrEl.textContent = "No Such Member in the club. Please type a username of an exiting member ";
    else if (errMsg === "passwordErr")
        passwordErrEl.textContent = "Wrong Password! Please try again";
}