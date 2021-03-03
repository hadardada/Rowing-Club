const reservationFormEl = document.querySelector('#mergeRes');
const addTitleEl = document.querySelector('#selectAddRow');
const noResEl = document.querySelector('#selectMergeRes');
const backEl = document.querySelector('#backButton');
const divFormBlock = document.querySelector('#formBlock');


const MergeResListContainerEl = document.querySelector('.mergeRes');
const wantedRowersMergeListContainerEl = document.querySelector('.wantedRowersMergedList');
const wantedRowersOriginalListContainerEl = document.querySelector('.wantedRowersOriginalList');

const mergeAction = document.createElement('button');
mergeAction.innerText = 'Merge Reservation'
mergeAction.style.position = 'absolute';
mergeAction.style.left = '5px'
reservationFormEl.append(mergeAction);
mergeAction.addEventListener('click', submitMergeReservation);
mergeAction.disabled = true;

//global
let resObjMerged;
let wantedRowersOriginal = new Array();
let wantedRowersMerged = new Array();

let membersCount = 0;

const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let createdOnId = urlParams.get('createdOn');
let creator = urlParams.get('creator');
let date = urlParams.get('date');

backEl.href = '/reservation/showSingle.html?creator=' + creator + '&createdOn=' + createdOnId + '&date=' + date;

const formErrorEl = document.querySelector('#errorSpan');
const addedMsgEl = document.querySelector('#addedMsgSpan')

const NO_ERROR = '';

main()

async function main(){
    await showWantedRowersOriginal();
}

//////////////////////////////////////////////////////////////////// display wanted Original  ///////////////////////////////

async function showWantedRowersOriginal(){
    const response = await fetch('/reservation/merge?creator='+creator+'&createdOn='+createdOnId+'&date='+date, {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    const value = await response.json();
    createMemberList(value);
    await showAllMatchRes();
}

function createMemberList(membersList) {
    wantedRowersOriginalListContainerEl.innerHTML = '';

    // Create Elements on from data
    membersCount = membersList.length;
        membersList.forEach((member) => {
        const memberEl = createMemberElement(member);
        wantedRowersOriginalListContainerEl.append(memberEl);
        const nameTitle = document.createElement("br");
        wantedRowersOriginalListContainerEl.append(nameTitle);
    });

}

function additionalMembers()
{
    if (this.checked){
        wantedRowersOriginal.push(this.id.substring('ORG'.length));
    }
    else {
        const index = wantedRowersOriginal.indexOf(this.id.substring('ORG'.length));
        if (index > -1) {
            wantedRowersOriginal.splice(index, 1);
        }
    }
}

function createMemberElement(member) {

    const el = document.createElement("p");

    const checkBoxMemberEl = document.createElement('input');
    checkBoxMemberEl.setAttribute("type", "checkBox");
    checkBoxMemberEl.setAttribute("name", "mainMember");
    checkBoxMemberEl.id = "ORG"+member.email;
    checkBoxMemberEl.addEventListener('change', additionalMembers);
    checkBoxMemberEl.style.position = 'absolute';
    checkBoxMemberEl.style.left = '5px'
    el.append(checkBoxMemberEl);

    const nameEl = document.createElement('span');
    nameEl.innerText = "Name: " + member.name;
    el.append(nameEl);
    nameEl.style.position = 'absolute';
    nameEl.style.left = '30px'

    const idEl = document.createElement('span');
    idEl.innerText = "Email: " + member.email;
    el.appendChild(idEl)
    idEl.style.position = 'absolute';
    idEl.style.left = '200px'

    if (membersCount===1){
        checkBoxMemberEl.indeterminate = true;
    }
    membersCount--;
    return el
}

//////////////////////////////////////////////////////////////////// Display Match Reservations  /////////////////////////////////////////////////////////////

async function showAllMatchRes() {
    const response = await fetch('/reservation/pendingResSameActivity?creator='+creator+'&createdOn='+createdOnId+'&date='+date);
    const resJson = await response.json();
    if (resJson.length===0){
        noResEl.innerText = "No Match Reservation To Merge with, Can't Merge!"
        noResEl.style.color = "red"
    }
    else {
        createResList(resJson);
    }
}

function createResList(resList) {
    MergeResListContainerEl.innerHTML = '';

    // Create Elements on from data
    resList.forEach((res) => {
        const resEl = createResElement(res);
        MergeResListContainerEl.append(resEl);
        const nameTitle = document.createElement("br");
        MergeResListContainerEl.append(nameTitle);
    });
    const newLine = document.createElement("br");
    MergeResListContainerEl.append(newLine);
}

async function resToMerge(res) ///to complete
{
    resObjMerged = res;
    createWantedList(res.wantedMemberEmails,res.wantedMemberNames);
    mergeAction.disabled = false;
}


function createResElement(res) {

    const el = document.createElement("div");

    //add action button to each element
    const radioResEl = document.createElement('input');
    radioResEl.setAttribute("type", "radio");
    radioResEl.setAttribute("name", "mergedRes");
    radioResEl.id = res.getReservationMadeAt;

    radioResEl.addEventListener('change', function() {resToMerge(res);},false);
    radioResEl.style.position = 'absolute';
    radioResEl.style.left = '5px'
    radioResEl.required = true;
    el.appendChild(radioResEl);

    const mainRowerEl = document.createElement('span');
    mainRowerEl.innerText = "Main Rower: " + res.participantRowerEmail;
    el.appendChild(mainRowerEl);
    mainRowerEl.style.position = 'absolute';
    mainRowerEl.style.left = '30px'

    const newLine = document.createElement('br');
    el.appendChild(newLine);

    const additionalRowers = document.createElement('span');
    additionalRowers.innerText = "Additional Rowers: ";
    el.appendChild(additionalRowers)
    res.wantedMemberEmails.forEach((member) => {
        const memberEmailEl = document.createElement('span');
        memberEmailEl.innerText = member + '  ';
        additionalRowers.append(memberEmailEl);
      //  memberEmailEl.style.position = 'absolute';
     //   memberEmailEl.style.left = '80px';

    });
    additionalRowers.style.position = 'absolute';
    additionalRowers.style.left = '30px';
    return el
}

//////////////////////////////////////////////////////////////////// Display Wanted List  /////////////////////////////////////////////////////////////

function createWantedList(emailList,nameList) {
    wantedRowersMergeListContainerEl.innerHTML = '';

    // Create Elements on from data
    const memberEl = createMemberResElement(resObjMerged.participantRowerEmail,resObjMerged.participantRowerName);
    wantedRowersMergeListContainerEl.append(memberEl);
    const nameTitle = document.createElement("br");
    wantedRowersMergeListContainerEl.append(nameTitle);

    let numOfRow = emailList.length;
    for (let i = 0;i < numOfRow; i++) {
        const memberEl = createMemberResElement(emailList[i],nameList[i]);
        wantedRowersMergeListContainerEl.append(memberEl);
        const nameTitle = document.createElement("br");
        wantedRowersMergeListContainerEl.append(nameTitle);
    }
}

function additionalMergeMembers()
{
    if (this.checked){
        wantedRowersMerged.push(this.id.substring('MRG'.length));
    }
    else {
        const index = wantedRowersMerged.indexOf(this.id.substring('MRG'.length));
        if (index > -1) {
            wantedRowersMerged.splice(index, 1);
        }
    }
}

function createMemberResElement(emailList,nameList) {

    const el = document.createElement("p");

    const checkBoxMemberEl = document.createElement('input');
    checkBoxMemberEl.setAttribute("type", "checkBox");
    checkBoxMemberEl.setAttribute("name", "MergeMember");
    checkBoxMemberEl.id = "MER"+ emailList;
    checkBoxMemberEl.addEventListener('change', additionalMergeMembers);
    checkBoxMemberEl.style.position = 'absolute';
    checkBoxMemberEl.style.left = '5px'
    el.append(checkBoxMemberEl);

    const nameEl = document.createElement('span');
    nameEl.innerText = "Name: " + nameList;
    el.append(nameEl);
    nameEl.style.position = 'absolute';
    nameEl.style.left = '30px'

    const idEl = document.createElement('span');
    idEl.innerText = "Email: " + emailList;
    el.appendChild(idEl)
    idEl.style.position = 'absolute';
    idEl.style.left = '200px'

    return el
}


//////////////////////////////////////////////////////////////////// submit Form  /////////////////////////////////////////////////////////////
function MergeJson() {
    this.createdOnMerge = resObjMerged.getReservationMadeAt;
    this.createdByMerge = resObjMerged.reservationMadeBy;
    this.wantedMerge = wantedRowersMerged;
    this.wantedOriginal = wantedRowersOriginal;
}

async function submitMergeReservation()
{
    const newMerge= new MergeJson();

    const response = await fetch('/reservation/merge?creator='+creator+'&createdOn='+createdOnId+'&date='+date, {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(newMerge)
    });

    if (response.status === 200)
    {
        divFormBlock.style.display = "none";
        addedMsgEl.textContent = "Reservations Merged successfully"
    }
    else{
        formErrorEl.textContent = "ERROR! " + await response.text();
        formErrorEl.style.color = "red";
    }
}

function showError(errorMsg) {
    let initMsg = "";
    if (errorMsg !== NO_ERROR)
        initMsg ="Cannot Merge Reservation: ";
    formErrorEl.textContent = initMsg+ errorMsg;
}