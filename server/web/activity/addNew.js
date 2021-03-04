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

const errorMsgEl = document.querySelector('#errorSpan');
const addedMsgEl = document.querySelector('#addedMsgSpan')


addNewActivityFormEl.addEventListener('submit', validateForm);

function boatTypeChecked() {
    // Get the checkbox
    let checkBox = boatTypeCheckBoxEl;
    // Get the output text
    let boatType = document.getElementById("boatTypeform");

    // If the checkbox is checked, display the output text
    if (checkBox.checked == true){
        boatType.style.display = "block";
        oneOarRadioEl.required = true;
        coxswainRadioEl.required = true;
        widthRadioEl.required = true;
        coastalRadioEl.required = true;
        oneRowerRadioEl.required = true;

    } else {
        boatType.style.display = "none";
    }
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

