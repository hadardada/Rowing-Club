
const privateCheckBoxEl = document.querySelector('#checkPrivate');
privateCheckBoxEl.addEventListener('change', privateChecked);

const activityName = document.querySelector('#activityName');
const startTime = document.querySelector('#StartTime');
const endTime = document.querySelector('#EndTime');
const boatTypes = document.querySelector('#EndTime');

boatFormEl.addEventListener('submit', validateForm);

const END_BEFORE_START = "End Time before Start Time ";
const NO_ERROR = '';

function validateForm(event) {

    if (startTime.value > endTime.value)
    {
        event.preventDefault();
        showError(END_BEFORE_START);
    }
    else
        showError(NO_ERROR);
}

async function submitActivity (name, startTime, endTime, boatTypes)
{
    const newActivity = {
        activityName:name,
        startTime:startTime,
        endTime: endTime,
        boatType: boatTypes
    }
}
    const response = await fetch('/activity/addNew', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(newBoat)
    });

    const result = await response.status;
    if (result === 200)
    {
        boatFormEl.style.display = "none";
        addedMsgEl.textContent = "A new activity was successfully added to the Club Calendar!"
    }

function showError(errorMsg) {
    let initMsg = "";
    if (errorMsg !== NO_ERROR)
        initMsg ="Cannot Add Activity: "
    formErrorEl.textContent = initMsg+ errorMsg;
}