const TWO_CHECKED = "You must check only one check box";

const activityNameEl = document.querySelector('#activityName');
const startTimeEl = document.querySelector('#startTime');
const endTimeEl = document.querySelector('#endTime');
const boatTypeEl = document.querySelector('#boatType');

const setBoatTypeCheckBoxEl = document.querySelector('#setBoatType');
const noBoatTypeCheckBoxEl = document.querySelector('#noBoatType');
const updateNoTypeBttnEl = document.querySelector('#noBoatTypeBttn');
const boatTypeFormEl = document.querySelector('#boatTypeform');

const errorMsgEl = document.querySelector('#errorMsg');

window.addEventListener('DOMContentLoaded',injectParameters);
setBoatTypeCheckBoxEl.addEventListener('change', showBoatTypeForm);
noBoatTypeCheckBoxEl.addEventListener('change', showBoatTypeForm);

//get activity Id num from url
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
const activityId = urlParams.get('id');

async function injectParameters(){

    const response = await fetch('/activity/edit?id='+activityId);
    if (response.status === 200) {
        const activityJson = await response.json();
        activityNameEl.textContent = activityJson.activityName;
        boatTypeEl.textContent = activityJson.boatName;
        startTimeEl.textContent = activityJson.startTime;
        endTimeEl.textContent = activityJson.endTime;
    }
    else{
        errorMsgEl.textContent = "Error! "+ await response.text();
        errorMsgEl.style.color="red";

    }
}

function showBoatTypeForm(){
    const checkBoxesErrEl = document.querySelector('#checkBoxError');
    checkBoxesErrEl.textContent=''; // reset whenever a change accrued
    if (setBoatTypeCheckBoxEl.checked && noBoatTypeCheckBoxEl.checked) // both check box are checked = impossible!
    {
        checkBoxesErrEl.textContent=TWO_CHECKED;
        boatTypeFormEl.style.display = "none";
        updateNoTypeBttnEl.style.display = "none";

    }
    else if (setBoatTypeCheckBoxEl.checked)
    {
        boatTypeFormEl.style.display = "block";
    }
    this.style.display = "block";
}

