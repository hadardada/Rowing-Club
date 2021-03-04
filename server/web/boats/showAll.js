const guestListContainerEl = document.querySelector('.boatsList');
const deleteButtonsElements = document.querySelector('.deleteButtons');
const titlesEl = document.querySelector('#titles');
let counter = 1;


refreshListUsesAsyncAwait()

async function refreshListUsesAsyncAwait() {
    const response = await fetch('/boathouse/boats/showAll', {
        method: 'get',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
    });
    const value = await response.json();
    createBoatList(value);
}

async function deleteBoat(){
    let id = this.id;
    const response = await fetch('/boathouse/boats/delete', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(id)
    });
    counter = 1;
    refreshListUsesAsyncAwait()
}

async function editBoat(){
    let boatId = this.id.substring('edit'.length)
    window.location.href = '/boats/edit.html?boatId='+boatId;

}

function createBoatElement(boat) {

    const el = document.createElement("p");

    //add action button to each element
    const deleteAction = document.createElement('button');
    deleteAction.innerText = 'delete'
    deleteAction.className = 'deleteButtons';
    deleteAction.id = boat.idNum;
    deleteAction.addEventListener('click', deleteBoat);

    deleteAction.style.position = 'absolute';
    deleteAction.style.left = '5px'
    el.append(deleteAction);

    const editAction = document.createElement('button');
    editAction.innerText = 'edit'
    editAction.style.position = 'absolute';
    editAction.style.left = '57px'
    editAction.id = "edit"+boat.idNum;
    editAction.addEventListener('click', editBoat);

    el.append(editAction);

    const number = document.createElement("span")
    number.innerText = " "+counter.toString()+'.';
    el.append(number);
    counter++;
    number.style.position = 'absolute';
    number.style.left = '115px'

    const nameEl = document.createElement('span');
    nameEl.innerText = boat.boatName;
    el.append(nameEl);
    nameEl.style.position = 'absolute';
    nameEl.style.left = '150px'

    const idEl = document.createElement('span');
    idEl.innerText = boat.idNum;
    el.appendChild(idEl)
    idEl.style.position = 'absolute';
    idEl.style.left = '250px'

    const boatTypeEl = document.createElement('span');
    boatTypeEl.innerText = boat.shortName;
    el.appendChild(boatTypeEl)
    boatTypeEl.style.position = 'absolute';
    boatTypeEl.style.left = '350px'

    const privateProp = document.createElement('span');
    if (boat.privateProperty){
        privateProp.innerText = 'YES';
    }
    else {
        privateProp.innerText = 'NO';
    }
    el.appendChild(privateProp);
    privateProp.style.position = 'absolute';
    privateProp.style.left = '455px'

    const outOfOrder = document.createElement('span');
    if (boat.status){
        outOfOrder.innerText = 'YES';
    }
    else {
        outOfOrder.innerText = 'NO';
    }
    el.appendChild(outOfOrder);
    outOfOrder.style.position = 'absolute';
    outOfOrder.style.left = '550px'

    return el;
}

function createBoatList(boatList) {
    guestListContainerEl.innerHTML = '';
    const nameTitle = document.createElement("span");
    nameTitle.innerText = ' NAME '
    const idTitle = document.createElement("span");
    idTitle.innerText = ' ID '
    const typeTitle = document.createElement("span");
    typeTitle.innerText = ' BOAT TYPE '
    const privateTitle = document.createElement("span");
    privateTitle.innerText = ' PRIVATE? '
    const OORTitle = document.createElement("span");
    OORTitle.innerText = ' OUT OF ORDER? '
    nameTitle.append(idTitle,typeTitle,privateTitle,OORTitle);
    titlesEl.append(nameTitle);
    nameTitle.style.position = 'absolute';
    nameTitle.style.left = '150px'
    nameTitle.style.top = '100px'
    nameTitle.style.textDecoration = "underline";
    idTitle.style.position = 'absolute';
    idTitle.style.left = '100px'
    idTitle.style.textDecoration = "underline";
    typeTitle.style.position = 'absolute';
    typeTitle.style.left = '200px'
    typeTitle.style.textDecoration = "underline";
    privateTitle.style.position = 'absolute';
    privateTitle.style.left = '300px'
    privateTitle.style.textDecoration = "underline";
    OORTitle.style.position = 'absolute';
    OORTitle.style.left = '400px'
    OORTitle.style.textDecoration = "underline";
    guestListContainerEl.style.position = 'absolute';
    guestListContainerEl.style.top = '100px'


    // Create Elements on from data
    boatList.forEach((boat) => {
        const boatEl = createBoatElement(boat);
        guestListContainerEl.append(boatEl);
        const nameTitle = document.createElement("br");
        guestListContainerEl.append(nameTitle);
    });
    const Title = document.createElement("br");
    guestListContainerEl.append(Title);
    const backEl = document.createElement("a");
    backEl.style.position = 'absolute';
    backEl.style.left = '5px';
    backEl.style.fontSize = 'small';
    backEl.href = "/menu/boats.html";
    backEl.innerText = "Go Back";
    guestListContainerEl.append(backEl);
}