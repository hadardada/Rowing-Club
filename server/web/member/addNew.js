
const privateCheckBoxEl = document.querySelector('#checkPrivate');
privateCheckBoxEl.addEventListener('change', privateChecked);

//radios consts
const memberFormEl = document.querySelector('#addNewMemberForm');
const divFormBlock = document.querySelector('#formBlock');
const memberAge = document.querySelector('#MemberAge');
const memberEmail = document.querySelector('#MemberEmail');
const memberPhoneNum = document.querySelector('#MemberPhoneNum');
const memberPassword = document.querySelector('#MemberPassword');
const memberName = document.querySelector('#MemberName');
const memberLevel1 = document.querySelector('#Beginner');
const memberLevel2 = document.querySelector('#Mid');
const managerYesEl = document.querySelector('#managerYes');
const memberNotes = document.querySelector('#MemberNotes');
const serNumEl = document.querySelector('#boatSerNum');




const formErrorEl = document.querySelector('#errorSpan');
const addedMsgEl = document.querySelector('#addedMsgSpan')
memberFormEl.addEventListener('submit', validateForm);

const EMAIL_FORMAT = "Email Address is wrong";
const MEMBER_AGE = "Members age is from 10 to 120";
const PHONE_NUM_DIGITS = "Phone number Digits numbers is not 10";
const PHONE_NUM_FORMAT = 'It Looks Like the Phone number Address is wrong';
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

    if (!(memberEmail.value.includes('@'))) {
        event.preventDefault();
        showError(EMAIL_FORMAT);

    }else if (parseInt(memberAge.value) < 10 || parseInt(memberAge.value) > 120) {
        event.preventDefault();
        showError(MEMBER_AGE);

    }else if (memberPhoneNum.value.length !== 10) {
        event.preventDefault();
        showError(PHONE_NUM_DIGITS);
    }
    else if (memberPhoneNum.value.match(/^[0-9]+$/) == null){
        event.preventDefault();
        showError(PHONE_NUM_FORMAT);
    }
    else{
        let level = '3';
        let serNum = '-1';
        if (memberLevel1.checked)
            level = memberLevel1.value;
        else if (memberLevel2.checked)
            level = memberLevel2.value;
        if (privateCheckBoxEl.checked)
            serNum = serNumEl.value;
        submitMember(memberName.value,memberNotes.value,memberEmail.value,memberPassword.value,memberAge.value,memberPhoneNum.value,privateCheckBoxEl.checked,serNum,level,managerYesEl.checked);
    }
    event.preventDefault();

}

function MemberJson(name, notes, email, password, age, phoneNumber, havePrivateBoat, privateBoatSerialNumber, rowingLevel,isManager) {
    this.name = name;
    this.notes = notes;
    this.email = email;
    this.password = password;
    this.age = age;
    this.phoneNumber = phoneNumber;
    this.havePrivateBoat = havePrivateBoat;
    this.privateBoatSerialNumber = privateBoatSerialNumber;
    this.rowingLevel = rowingLevel;
    this.isManager = isManager;
}


async function submitMember (name, notes, email, password, age, phoneNumber, havePrivateBoat, privateBoatSerialNumber, rowingLevel,isManager)
{
    const newMember = new MemberJson(name, notes, email, password, age, phoneNumber, havePrivateBoat, privateBoatSerialNumber, rowingLevel,isManager);

    const response = await fetch('/member/addNew', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(newMember)
    });

    if (response.ok)
    {
        divFormBlock.style.display = "none";
        addedMsgEl.textContent = "A new Member was successfully added to the club!"
    }

}

function showError(errorMsg) {
    let initMsg = "";
    if (errorMsg !== NO_ERROR)
        initMsg ="Cannot Add Member: ";
    formErrorEl.textContent = initMsg+ errorMsg;
}