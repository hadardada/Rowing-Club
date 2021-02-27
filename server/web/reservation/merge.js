const reservationFormEl = document.querySelector('#approveRes');
reservationFormEl.addEventListener('submit', submitMergeReservation);

const MergeResListContainerEl = document.querySelector('.mergeRes');
const wantedRowersMergeListContainerEl = document.querySelector('.wantedRowersMergedList');
const wantedRowersOriginalListContainerEl = document.querySelector('.wantedRowersOriginalList');

//global
let resObjMerged;
let wantedRowersOriginal = new Array();
let wantedRowersMerged = new Array();

let membersCount = 0;
let boatToApproveMaxRowers;
let resCounter = 0;

const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let createdOnId = urlParams.get('createdOn');
let creator = urlParams.get('creator');
let date = urlParams.get('date');


const formErrorEl = document.querySelector('#errorSpan');
const addedMsgEl = document.querySelector('#addedMsgSpan')

const TOO_MUCH_MEMBERS = "Additional Members Pass the Boat Limit";
const NO_ERROR = '';

main()

async function main(){
    await showWantedRowersOriginal();
}

//////////////////////////////////////////////////////////////////// display wanted Original  ///////////////////////////////

async function showWantedRowersOriginal(){
    const response = await fetch('/reservation/merge', {
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
        const index = wantedRowersOriginal.indexOf(this.id);
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
    const response = await fetch('/reservation/pendingResSameActivity?creator='+creator+'createdOn='+createdOnId+'date='+date);
    const resJson = await response.json();
    createResList(resJson);
    await showAllDate();
}

function createResList(resList) {
    BoatListContainerEl.innerHTML = '';

    // Create Elements on from data
    resList.forEach((res) => {
        const resEl = createResElement(res);
        MergeResListContainerEl.append(resEl);
        const nameTitle = document.createElement("br");
        MergeResListContainerEl.append(nameTitle);
    });
}

async function resToMerge(res) ///to complete
{
    resObjMerged = res;
    createWantedList(res.wantedMemberEmails,res.wantedMemberNames);
}



async function showMore()
{
        let id = this.id;
        const response = await fetch('/reservation/showSingle?creator='+creator+'createdOn='+createdOnId+'date='+date, {
            method: 'get',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
        });
}

function createResElement(res) {

    const el = document.createElement("p");

    //add action button to each element
    const radioResEl = document.createElement('input');
    radioResEl.setAttribute("type", "radio");
    radioResEl.setAttribute("name", "mergedRes");
    radioResEl.id = res.getReservationMadeAt;

    radioResEl.addEventListener('change', function() {resToMerge(res);},false);
    radioResEl.style.position = 'absolute';
    radioResEl.style.left = '5px'
    if (resCounter === 0){
        radioResEl.defaultChecked;
    }
    resCounter++;
    el.append(radioResEl);

    const mainRowerEl = document.createElement('span');
    mainRowerEl.innerText = "Main Rower: " + res.participantRowerEmail;
    el.append(mainRowerEl);
    mainRowerEl.style.position = 'absolute';
    mainRowerEl.style.left = '30px'

    const additionalRowers = document.createElement('span');
    additionalRowers.innerText = "Additional Rowers: ";
    el.appendChild(additionalRowers)
    let distanceNum = 100;
    let distance = distanceNum + 'px';
    res.wantedMemberEmails.forEach((member) => {

        const memberEmailEl = document.createElement('span');
        memberEmailEl.innerText = "Email: " + member;
        additionalRowers.append(memberEmailEl);
        memberEmailEl.style.position = 'absolute';
        memberEmailEl.style.left = distance;
        distanceNum+=10;
    });
    additionalRowers.style.position = 'absolute';
    additionalRowers.style.left = '100px';

    const showAction = document.createElement('button');
    showAction.innerText = 'Show More'
    showAction.className = 'deleteButtons';
    showAction.id = showAction.email;
    showAction.addEventListener('click', showMore);

    showAction.style.position = 'absolute';
    showAction.style.left = distanceNum;
    el.append(showAction);

    return el
}

//////////////////////////////////////////////////////////////////// Display Wanted List  /////////////////////////////////////////////////////////////

function createWantedList(emailList,nameList) {
    wantedRowersMergeListContainerEl.innerHTML = '';

    // Create Elements on from data
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
        const index = wantedRowersMerged.indexOf(this.id);
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

    const response = await fetch('/reservation/merge', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(newMerge)
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
        initMsg ="Cannot Approve Member: ";
    formErrorEl.textContent = initMsg+ errorMsg;
}