
function showRowingLevel(level, rowingEl)
{
    if ((level === 1)||(level === '1'))
        rowingEl.textContent = 'Beginner';
    else if ((level === 2)|(level === '2'))
        rowingEl.textContent = 'Mid';
    else
        rowingEl.textContent = 'Expert';
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
    this.signUpDate = '';
    this.expirationDate = '';
}

function privateChecked() {
    // Get the checkbox
    let checkBox = this;
    // Get the output text
    let boatOwner = document.getElementById("boatOwnerform");

    // If the checkbox is checked, display the output text
    if (checkBox.checked == true){
        boatOwner.style.display = "block";
    } else {
        boatOwner.style.display = "none";
    }
}