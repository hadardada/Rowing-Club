const guestListContainerEl = document.querySelector('.membersList');
const deleteButtonsElements = document.querySelector('.deleteButtons');
const titlesEl = document.querySelector('#titles');
let counter = 1;


refreshListUsesAsyncAwait()

async function refreshListUsesAsyncAwait() {
    const response = await fetch('/member/showAll', {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    const value = await response.json();
    createMemberList(value);
}

// async function deleteActivity(id){
//     const response = await fetch('/activity/delete', {
//         method: 'post',
//         headers: new Headers({
//             'Content-Type': 'application/json;charset=utf-8'
//         }),
//         body: JSON.stringify(id)
//     });
// }


function createMemberElement(member) {

    const el = document.createElement("p");

    //add action button to each element
    const deleteAction = document.createElement('button');
    deleteAction.innerText = 'delete'
    deleteAction.className = 'deleteButtons';
    deleteAction.id = 'deleteButtons' + member.id;
    // deleteAction.onclick = deleteActivity(activity.id);
    //   deleteAction.addEventListener('change', deleteActivity(boat.id));
    deleteAction.style.position = 'absolute';
    deleteAction.style.left = '5px'
    el.append(deleteAction);

    const editAction = document.createElement('button');
    editAction.innerText = 'edit'
    editAction.style.position = 'absolute';
    editAction.style.left = '57px'
    el.append(editAction);

    const number = document.createElement("span")
    number.innerText = " "+counter.toString()+'.';
    el.append(number);
    counter++;
    number.style.position = 'absolute';
    number.style.left = '115px'

    const nameEl = document.createElement('span');
    nameEl.innerText = member.name;
    el.append(nameEl);
    nameEl.style.position = 'absolute';
    nameEl.style.left = '150px'

    const idEl = document.createElement('span');
    idEl.innerText = member.email;
    el.appendChild(idEl)
    idEl.style.position = 'absolute';
    idEl.style.left = '250px'

    const boatTypeEl = document.createElement('span');
    boatTypeEl.innerText = member.password;
    el.appendChild(boatTypeEl)
    boatTypeEl.style.position = 'absolute';
    boatTypeEl.style.left = '400px'

    const ageEl = document.createElement('span');
    ageEl.innerText = member.age;
    el.appendChild(ageEl)
    ageEl.style.position = 'absolute';
    ageEl.style.left = '500px'

    const phoneEl = document.createElement('span');
    phoneEl.innerText = member.phoneNumber;
    el.appendChild(phoneEl)
    phoneEl.style.position = 'absolute';
    phoneEl.style.left = '600px'

    const rowingEl = document.createElement('span');
    if (member.rowingLevel === 1)
        rowingEl.innerText = 'Begginer';
    else if (member.rowingLevel === 2)
        rowingEl.innerText = 'Mid';
    else
        rowingEl.innerText = 'Expert';
    el.appendChild(rowingEl)
    rowingEl.style.position = 'absolute';
    rowingEl.style.left = '700px'

    const privateProp = document.createElement('span');
    if (member.havePrivateBoat){
        privateProp.innerText = 'YES';
    }
    else {
        privateProp.innerText = 'NO';
    }
    el.appendChild(privateProp);
    privateProp.style.position = 'absolute';
    privateProp.style.left = '800px'

    const privatePropSerNum = document.createElement('span');
    if (member.havePrivateBoat){
        privatePropSerNum.innerText = member.privateBoatSerialNumber.toString();
    }
    else {
        privatePropSerNum.innerText = '-';
    }
    el.appendChild(privatePropSerNum);
    privatePropSerNum.style.position = 'absolute';
    privatePropSerNum.style.left = '900px'


    const manager = document.createElement('span');
    if (member.isManager){
        manager.innerText = 'YES';
    }
    else {
        manager.innerText = 'NO';
    }
    el.appendChild(manager);
    manager.style.position = 'absolute';
    manager.style.left = '1000px';

    const signUpDateEl = document.createElement('span');
    signUpDateEl.innerText = member.signUpDate;
    el.appendChild(signUpDateEl)
    signUpDateEl.style.position = 'absolute';
    signUpDateEl.style.left = '1100px'

    const experarionEl = document.createElement('span');
    experarionEl.innerText = member.expirationDate;
    el.appendChild(experarionEl)
    experarionEl.style.position = 'absolute';
    experarionEl.style.left = '1200px'

    const notesEl = document.createElement('span');
    notesEl.innerText = member.notes;
    el.appendChild(notesEl)
    notesEl.style.position = 'absolute';
    notesEl.style.left = '1300px'

    return el;
}

function createMemberList(memberList) {
    guestListContainerEl.innerHTML = '';
    const nameTitle = document.createElement("span");
    nameTitle.innerText = ' NAME '
    const idTitle = document.createElement("span");
    idTitle.innerText = ' EMAIL '
    const passTitle = document.createElement("span");
    passTitle.innerText = ' PASSWORD '
    const phoneTitle = document.createElement("span");
    phoneTitle.innerText = ' PHONE NUMBER'
    const ageTitle = document.createElement("span");
    ageTitle.innerText = ' AGE '
    const privateTitle = document.createElement("span");
    privateTitle.innerText = ' PRIVATE BOAT? '
    const privateNumTitle = document.createElement("span");
    privateNumTitle.innerText = ' PRIVATE BOAT SERIAL NUMBER '
    const levelTitle = document.createElement("span");
    levelTitle.innerText = ' ROWING LEVEL '
    const managerTitle = document.createElement("span");
    managerTitle.innerText = ' MANAGER? '
    const signUpDateTitle = document.createElement("span");
    signUpDateTitle.innerText = ' SIGNUP DATE '
    const signUpDate = document.createElement("span");
    signUpDate.innerText = ' EXPIRATION DATE '
    const notesTitle = document.createElement("span");
    notesTitle.innerText = ' NOTES '

    nameTitle.append(idTitle,passTitle,ageTitle,phoneTitle,levelTitle,privateTitle,privateNumTitle,managerTitle,signUpDateTitle,signUpDate,notesTitle);
    titlesEl.append(nameTitle);
    nameTitle.style.position = 'absolute';
    nameTitle.style.left = '150px'
    nameTitle.style.textDecoration = "underline";

    idTitle.style.position = 'absolute';
    idTitle.style.left = '100px'
    idTitle.style.textDecoration = "underline";

    passTitle.style.position = 'absolute';
    passTitle.style.left = '250px'
    passTitle.style.textDecoration = "underline";

    ageTitle.style.position = 'absolute';
    ageTitle.style.left = '350px'
    ageTitle.style.textDecoration = "underline";

    phoneTitle.style.position = 'absolute';
    phoneTitle.style.left = '450px'
    phoneTitle.style.textDecoration = "underline";

    levelTitle.style.position = 'absolute';
    levelTitle.style.left = '550px'
    levelTitle.style.textDecoration = "underline";

    privateTitle.style.position = 'absolute';
    privateTitle.style.left = '650px'
    privateTitle.style.textDecoration = "underline";

    privateNumTitle.style.position = 'absolute';
    privateNumTitle.style.left = '750px'
    privateNumTitle.style.textDecoration = "underline";

    managerTitle.style.position = 'absolute';
    managerTitle.style.left = '850px'
    managerTitle.style.textDecoration = "underline";

    signUpDateTitle.style.position = 'absolute';
    signUpDateTitle.style.left = '950px'
    signUpDateTitle.style.textDecoration = "underline";

    signUpDate.style.position = 'absolute';
    signUpDate.style.left = '1050px'
    signUpDate.style.textDecoration = "underline";

    notesTitle.style.position = 'absolute';
    notesTitle.style.left = '1150px'
    notesTitle.style.textDecoration = "underline";


    guestListContainerEl.style.position = 'absolute';
    guestListContainerEl.style.top = '200px'


    // Create Elements on from data
    memberList.forEach((member) => {
        const memberEl = createMemberElement(member);
        guestListContainerEl.append(memberEl);
        const nameTitle = document.createElement("br");
        guestListContainerEl.append(nameTitle);
    });
}