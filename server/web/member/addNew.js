
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

const EMAIL_FORMAT = "Email Format is Wrong";
const MEMBER_AGE = "Members age is from 10 to 120";
const PHONE_NUM_DIGITS = "Phone number Digits numbers is not 10";
const PHONE_NUM_FORMAT = 'It Looks Like the Phone number Address is wrong';
const NO_ERROR = '';


function validateForm(event) {
    let emailRegex = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    if (!emailRegex.test(memberEmail.value)) {
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

async function submitMember (name, notes, email, password, age, phoneNumber, havePrivateBoat, privateBoatSerialNumber, rowingLevel,isManager)
{
    const newMember = new MemberJson(name, notes, email, password, age, phoneNumber, havePrivateBoat, privateBoatSerialNumber, rowingLevel,isManager);

    const response = await fetch('/boathouse/member/addNew', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(newMember)
    });

    if (response.status === 200)
    {
        divFormBlock.style.display = "none";
        addedMsgEl.textContent = "A new Member was successfully added to the club!"
    }
    else{
        formErrorEl.textContent = "ERROR! " + await response.text();
        formErrorEl.style.color = "red";
    }
}

function showError(errorMsg) {
    let initMsg = "";
    if (errorMsg !== NO_ERROR)
        initMsg ="Cannot Add Member: ";
    formErrorEl.textContent = initMsg+ errorMsg;
}