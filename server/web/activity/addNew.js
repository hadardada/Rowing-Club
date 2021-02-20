const boatTypeCheckBoxEl = document.querySelector('#checkBoatType');
boatTypeCheckBoxEl.addEventListener('change', boatTypeChecked);

const addNewActivityFormEl = document.querySelector('#addNewActivityForm');

const activityName = document.querySelector('#activityName');
const divFormBlock = document.querySelector('#formBlock');
const startTime = document.querySelector('#StartTime');
const endTime = document.querySelector('#EndTime');
const oneRowerRadioEl = document.querySelector('#solo');
const twoRowerRadioEl = document.querySelector('#pair');
const eightRowerRadioEl = document.querySelector('#eight');
const coxswainRadioEl = document.querySelector('#coxswain');
const oneOarRadioEl = document.querySelector('#singleOar');
const coastalRadioEl = document.querySelector('#coastal');
const widthRadioEl = document.querySelector('#wide');

const formErrorEl = document.querySelector('#errorSpan');
const addedMsgEl = document.querySelector('#addedMsgSpan')


addNewActivityFormEl.addEventListener('submit', validateForm);

const END_BEFORE_START = "End Time before Start Time ";
const ONE_ROWER_ONE_OAR = "A boat with only one rower must be with two oars";
const ONE_ROWER_WITH_COXS = "A boat with only one rower must be coxless";
const EIGHT_ROWERS_NO_COXS = "A boat with eight rowers must be with a coxswain";
const NO_ERROR = '';

function boatTypeChecked() {
    // Get the checkbox
    let checkBox = boatTypeCheckBoxEl;
    // Get the output text
    let boatType = document.getElementById("boatTypeform");

    // If the checkbox is checked, display the output text
    if (checkBox.checked == true){
        boatType.style.display = "block";

    } else {
        boatType.style.display = "none";
    }
}

function validateForm(event) {

    if (startTime.value > endTime.value)
    {
        event.preventDefault();
        showError(END_BEFORE_START);
    }
    else if (oneRowerRadioEl.checked && oneOarRadioEl.checked) {
        //A boat with only one rower cannot be with single oar
        event.preventDefault();
        showError(ONE_ROWER_ONE_OAR);
    }else if (oneRowerRadioEl.checked && coxswainRadioEl.checked) {
        //A boat with only one rower cannot have a coxswain
        event.preventDefault();
        showError(ONE_ROWER_WITH_COXS);
    }else if (eightRowerRadioEl.checked && !coxswainRadioEl.checked) {
        //A boat with eight rowers cannot be coxless
        event.preventDefault();
        showError(EIGHT_ROWERS_NO_COXS);
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
        submitActivity(activityName.value, startTime.value, endTime.value, boatSize, oneOarRadioEl.checked, widthRadioEl.checked, coxswainRadioEl.checked, coastalRadioEl.checked,boatTypeCheckBoxEl.checked);
    }
    event.preventDefault();
}
function activityJson(name, startTime, endTime, boatSize, oneOar,width,coxswain, coastalboat,hasBoat) {
    this.activityName = name;
    this.startTime = startTime;
    this.endTime = endTime;
    this.rowersNum = boatSize;
    this.singleOar = oneOar;
    this.wide = width;
    this.helmsman = coxswain;
    this.coastal = coastalboat;
    this.hasBoat = hasBoat;
    this.boatName = '';
}

async function submitActivity (name, startTime, endTime, boatSize, oneOar,width,coxswain, coastalboat,hasBoat) {
    const newActivity = new activityJson(name, startTime, endTime, boatSize, oneOar,width,coxswain, coastalboat,hasBoat);

    const response = await fetch('/activity/addNew', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(newActivity)
    });

    if (response.ok)

    {
        divFormBlock.style.display = "none";
        addedMsgEl.textContent = "A new Activity was successfully added to the club!"
    }
}

function showError(errorMsg) {
    let initMsg = "";
    if (errorMsg !== NO_ERROR)
        initMsg = "Cannot Add Activity: "
    formErrorEl.textContent = initMsg + errorMsg;
}