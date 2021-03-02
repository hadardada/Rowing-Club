
const ManageExistEl = document.querySelector('#ManageExist');
const addNewEl = document.querySelector('#addNew');
const genDivEl = document.querySelector('#formBlock');
const goBackEl = document.querySelector('#goBackMain');


setDifferentLinkForNoneManager()
async function setDifferentLinkForNoneManager(){
    const response = await fetch('/isUserManager', {method: 'get'});
    const isManager = await response.text();
    if (isManager==='false'){
        ManageExistEl.href = "http://localhost:8080/reservation/showAll.html?status=future";
        ManageExistEl.innerText = 'Manage Future Reservations';
        const history = document.createElement("a")
        history.innerText = "Show Reservations History";
        history.href = "http://localhost:8080/reservation/showAll.html?status=history";
        genDivEl.append(history)
        goBackEl.href = "mainMember.html";
        addNewEl.style.top = "140px";
        history.style.position = 'absolute'
        history.style.top = "110px"
        history.style.left = "30px"
        history.style.fontSize = "large"
    }
}

