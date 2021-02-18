
const privateCheckBoxEl = document.querySelector('#checkPrivate');
privateCheckBoxEl.addEventListener('change', privateChecked);

const activityName = document.querySelector('#activityName');
const startTime = document.querySelector('#StartTime');
const endTime = document.querySelector('#EndTime');
const oneRowerRadioEl = document.querySelector('#solo');
const twoRowerRadioEl = document.querySelector('#pair');
const eightRowerRadioEl = document.querySelector('#eight');
const coxswainRadioEl = document.querySelector('#coxswain');
const oneOarRadioEl = document.querySelector('#singleOar');
const coastalRadioEl = document.querySelector('#coastal');
const widthRadioEl = document.querySelector('#wide');

boatFormEl.addEventListener('submit', validateForm);

const END_BEFORE_START = "End Time before Start Time ";
const NO_ERROR = '';

function showError(errorMsg) {
    let initMsg = "";
    if (errorMsg !== NO_ERROR)
        initMsg = "Cannot Add Activity: "
    formErrorEl.textContent = initMsg + errorMsg;
}

function validateForm(event) {

    if (startTime.value > endTime.value)
    {
        event.preventDefault();
        showError(END_BEFORE_START);
    }
    else {
        let boatSize;
        if (oneRowerRadioEl.checked)
            boatSize = oneRowerRadioEl.value;
        else if (twoRowerRadioEl.checked)
            boatSize = twoRowerRadioEl.value;
        else if (eightRowerRadioEl.checked)
            boatSize = eightRowerRadioEl.value;
        else
            boatSize = 'four';
        submitActivity(activityName.value, startTime.value, endTime.value, boatSize, oneOarRadioEl.checked, widthRadioEl.checked, coxswainRadioEl.checked, coastalRadioEl.checked);
    }
}

async function submitActivity (name, startTime, endTime, boatSize, oneOar,width,coxswain, coastalboat) {
    const newActivity = {
        activityName: name,
        startTime: startTime,
        endTime: endTime,
        rowersNum: boatSize,
        singleOar: oneOar,
        wide: width,
        helmsman: coxswain,
        coastal: coastalboat
    }
    const response = await fetch('/activity/addNew', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(newActivity)
    });

    const result = await response.status;
    if (result === 200) {
        boatFormEl.style.display = "none";
        addedMsgEl.textContent = "A new activity was successfully added to the Club Calendar!"
    }
}