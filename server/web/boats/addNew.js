
const privateCheckBoxEl = document.querySelector('#checkPrivate');
privateCheckBoxEl.addEventListener('change', privateChecked);

//radios consts
const boatFormEl = document.querySelector('#addNewBoatForm');
const divFormBlock = document.querySelector('#formBlock');
const boatNameEl = document.querySelector('#boatName');
const oneRowerRadioEl = document.querySelector('#solo');
const twoRowerRadioEl = document.querySelector('#pair');
const eightRowerRadioEl = document.querySelector('#eight');
const coxswainRadioEl = document.querySelector('#coxswain');
const oneOarRadioEl = document.querySelector('#singleOar');
const coastalRadioEl = document.querySelector('#coastal');
const widthRadioEl = document.querySelector('#wide');
const outOfOrderEl = document.querySelector('#outof');

//validation check and error message
const formErrorEl = document.querySelector('#errorSpan');
const addedMsgEl = document.querySelector('#addedMsgSpan')
boatFormEl.addEventListener('submit', validateForm);
const ONE_ROWER_ONE_OAR = "A boat with only one rower must be with two oars";
const ONE_ROWER_WITH_COXS = "A boat with only one rower must be coxless";
const EIGHT_ROWERS_NO_COXS = "A boat with eight rowers must be with a coxswain";
const NO_ERROR = '';


privateCheckBoxEl.addEventListener('change',privateChecked );

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
    else{
        let boatSize;
        if (oneRowerRadioEl.checked)
            boatSize = oneRowerRadioEl.value;
        else if (twoRowerRadioEl.checked)
            boatSize = twoRowerRadioEl.value;
        else if (eightRowerRadioEl.checked)
            boatSize = eightRowerRadioEl.value;
        else
            boatSize = 'four';
        submitBoat(boatNameEl.value, privateCheckBoxEl.checked, outOfOrderEl.checked, boatSize, oneOarRadioEl.checked, widthRadioEl.checked, coxswainRadioEl.checked, coastalRadioEl.checked);
        //resetForm();
        //refreshListUsesAsyncAwait();
        }
    event.preventDefault();
}

function BoatJson(boatName, privateProperty, status, rowersNum, singleOar, wide, helmsman, coastal) {
    this.boatName = boatName;
    this.idNum = 0;
    this.privateProperty = privateProperty;
    this.status = status;
    this.rowersNum = rowersNum;
    this.singleOar = singleOar;
    this.wide = wide;
    this.helmsman = helmsman;
    this.coastal = coastal;
    this.shortName = '';
}


async function submitBoat (name, isPrivate, isOutOfOrder, boatSize, oneOar, width, coxswain, coastalboat)
{
    const newBoat = new BoatJson(name, isPrivate, isOutOfOrder, boatSize, oneOar, width, coxswain, coastalboat);

    const response = await fetch('/boats/addNew', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(newBoat)
    });

    //const result = await response.status;
    if (response.ok)

    {
        divFormBlock.style.display = "none";
        addedMsgEl.textContent = "A new boat was successfully added to the club!"
    }

}

function showError(errorMsg) {
    let initMsg = "";
    if (errorMsg !== NO_ERROR)
        initMsg ="Cannot Add Boat: ";
    formErrorEl.textContent = initMsg+ errorMsg;
}