const END_BEFORE_START = "End Time before Start Time ";
const ONE_ROWER_ONE_OAR = "A boat with only one rower must be with two oars";
const ONE_ROWER_WITH_COXS = "A boat with only one rower must be coxless";
const EIGHT_ROWERS_NO_COXS = "A boat with eight rowers must be with a coxswain";
const NO_ERROR = '';



function validateForm(event) {

    if (this === addNewActivityFormEl){ // if the validity check is for sending a whole new activity
        if (validateTimes(startTime, endTime))
            event.preventDefault();}
    if (oneRowerRadioEl.checked && oneOarRadioEl.checked) {
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
        if (this === addNewActivityFormEl)
            submitActivity(activityName.value, startTime.value, endTime.value, boatSize, oneOarRadioEl.checked, widthRadioEl.checked, coxswainRadioEl.checked, coastalRadioEl.checked,boatTypeCheckBoxEl.checked);
        else
            return new activityJson (activityName.value, startTime.value, endTime.value, boatSize, oneOarRadioEl.checked, widthRadioEl.checked, coxswainRadioEl.checked, coastalRadioEl.checked,boatTypeCheckBoxEl.checked);
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

function validateTimes (starts, ends){
    if (starts.value > ends.value)
    {
        showError(END_BEFORE_START);
        return false;
    }
    return true;
}

function showError(errorMsg) {
    let initMsg = "";
    if (errorMsg !== NO_ERROR)
        initMsg = "Cannot Add Activity: "
    errorMsgEl.textContent = initMsg + errorMsg;
}