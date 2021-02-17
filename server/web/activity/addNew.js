
const privateCheckBoxEl = document.querySelector('#checkPrivate');
privateCheckBoxEl.addEventListener('change', privateChecked);

const boatFormEl = document.querySelector('#addNewBoatForm');
const oneRowerRadioEl = document.querySelector('#solo');
const eightRowerRadioEl = document.querySelector('#eight');
const coxswainRadioEl = document.querySelector('#coxswain');
const oneOarRadioEl = document.querySelector('#singleOar');
const formErrorEl = document.querySelector('#errorSpan');
boatFormEl.addEventListener('submit', validateForm);

const ONE_ROWER_ONE_OAR = "A boat with only one rower must be with two oars";
const ONE_ROWER_WITH_COXS = "A boat with only one rower must be coxless";
const EIGHT_ROWERS_NO_COXS = "A boat with eight rowers must be with a coxswain";
const NO_ERROR = '';

//
//privateCheckBoxEl.addEventListener('change', )

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

    else
        showError(NO_ERROR);
}



async function submitBoat (name, isPrivate, isOutOfOrder, boatSize, oneOar, width, coxswain, coastalboat)
{
    const newBoatType ={
        rowersNum:boatSize,
        singleOar:oneOar,
        wide:width,
        helmsman:coxswain,
        coastal:coastalboat
    }
    const newBoat = {
        boatName:name,
        privateProperty:isPrivate,
        type: newBoatType
    }
}

function showError(errorMsg) {
    let initMsg = "";
    if (errorMsg !== NO_ERROR)
        initMsg ="Cannot Add Boat: "
    formErrorEl.textContent = initMsg+ errorMsg;
}