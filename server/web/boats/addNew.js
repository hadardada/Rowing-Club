
const privateCheckBoxEl = document.querySelector('#checkPrivate');
privateCheckBoxEl.addEventListener('checkbox', )
function privateChecked() {
    // Get the checkbox
    let checkBox = document.getElementById("checkPrivate");
    // Get the output text
    let boatOwner = document.getElementById("boatOwner");

    // If the checkbox is checked, display the output text
    if (checkBox.checked == true){
        boatOwner.style.display = "block";
    } else {
        boatOwner.style.display = "none";
    }
}