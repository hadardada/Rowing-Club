
const privateCheckBoxEl = document.querySelector('#checkPrivate');
privateCheckBoxEl.addEventListener('change', privateChecked);

const activityName = document.querySelector('#activityName');
const startTime = document.querySelector('#StartTime');
const endTime = document.querySelector('#EndTime');
boatFormEl.addEventListener('submit', validateForm);

const END_BEFORE_START = "End Time before Start Time ";
const NO_ERROR = '';

function privateChecked() {
    // Get the checkbox
    let checkBox = privateCheckBoxEl;
    // Get the output text
    let boatOwner = document.getElementById("boatOwnerform");

    // If the checkbox is checked, display the output text
    if (checkBox.checked == true){
        boatOwner.style.display = "block";
    } else {
        boatOwner.style.display = "none";
    }
}

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

function showError(errorMsg) {
    let initMsg = "";
    if (errorMsg !== NO_ERROR)
        initMsg ="Cannot Add Activity: "
    formErrorEl.textContent = initMsg+ errorMsg;
}