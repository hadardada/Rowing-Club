const END_BEFORE_START = "End Time cannot be before Start Time ";
const ONE_ROWER_ONE_OAR = "A boat with only one rower must be with two oars";
const ONE_ROWER_WITH_COXS = "A boat with only one rower must be coxless";
const EIGHT_ROWERS_NO_COXS = "A boat with eight rowers must be with a coxswain";
const START_TIME_IN_FORMAT = 'Start time is not in the right format';
const END_TIME_IN_FORMAT = 'End time is not in the right format';
const NO_ERROR = '';



function validateForm(event) {
  //  if (this === addNewActivityFormEl){ // if the validity check is for sending a whole new activity

    if (!validateTimes(startTime, endTime)) {
        if (event !== undefined)
            event.preventDefault();
        showError(END_BEFORE_START);
    }
    else if (!startTime.value.match(/^$|^(([01][0-9])|(2[0-3])):[0-5][0-9]$/)) {
        if (event !== undefined)
            event.preventDefault();
        showError(START_TIME_IN_FORMAT);
    }
    else if (!endTime.value.match(/^$|^(([01][0-9])|(2[0-3])):[0-5][0-9]$/)) {
            if (event !== undefined)
                event.preventDefault();
            else
            showError(END_TIME_IN_FORMAT);
    }
    if (event !== undefined) {
        if (oneRowerRadioEl.checked && oneOarRadioEl.checked) {
            //A boat with only one rower cannot be with single oar
            event.preventDefault();
            showError(ONE_ROWER_ONE_OAR);
        } else if (oneRowerRadioEl.checked && coxswainRadioEl.checked) {
            //A boat with only one rower cannot have a coxswain
            event.preventDefault();
            showError(ONE_ROWER_WITH_COXS);
        } else if (eightRowerRadioEl.checked && !coxswainRadioEl.checked) {
            //A boat with eight rowers cannot be coxless
            event.preventDefault();
            showError(EIGHT_ROWERS_NO_COXS);
        } else {
            let boatSize;
            if (oneRowerRadioEl.checked)
                boatSize = oneRowerRadioEl.value;
            else if (twoRowerRadioEl.checked)
                boatSize = twoRowerRadioEl.value;
            else if (eightRowerRadioEl.checked)
                boatSize = eightRowerRadioEl.value;
            else
                boatSize = 'four';
            if (this === addNewActivityFormEl) {
                submitActivity(activityName.value, startTime.value, endTime.value, boatSize, oneOarRadioEl.checked, widthRadioEl.checked, coxswainRadioEl.checked, coastalRadioEl.checked, boatTypeCheckBoxEl.checked);
            }

            else
                return new activityJson(activityName.value, startTime.value, endTime.value, boatSize, oneOarRadioEl.checked, widthRadioEl.checked, coxswainRadioEl.checked, coastalRadioEl.checked, boatTypeCheckBoxEl.checked);
        }
    }
    if (event !== undefined) {
        event.preventDefault();
    }
    else
        return true;

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
    this.id = '';
}

function validateTimes (starts, ends) {
    let endString = ends.value;
    let startString= starts.value;

    if (typeof endString === 'undefined')
        endString = ends.textContent;
    if (typeof endString=== 'undefined')
        startString= starts.textContent;
    if (parseInt(endString.substring(0, 2)) > parseInt(startString.substring(0, 2)))
        return true;
    if (parseInt(endString.substring(0, 2)) === parseInt(starts.value.substring(0, 2))) {
        if (parseInt(startString.substring(3)) < parseInt(endString.substring(3)))
            return true;
    }
    return false;
    showError(END_BEFORE_START);
}


function showError(errorMsg) {
    let initMsg = "";
    if (errorMsg !== NO_ERROR)
        initMsg = "Cannot Add Activity: "
    errorMsgEl.textContent = initMsg + errorMsg;
}