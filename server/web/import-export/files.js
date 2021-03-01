const NO_ERROR = '';
const ERROR_HEAD = "The following problems have occurred during the Import Process from the given XML file:"
const TWO_CHECK= 'You must pick one action at a time';
const NO_FILE = "You must choose a file before uploading";
const importCheckboxEl = document.querySelector('#import');
const exportCheckboxEl = document.querySelector('#export');
const importFormEl = document.querySelector('#importForm');
const exportFormEl = document.querySelector('#exportForm');
const errorMsgEl = document.querySelector('#errorChoice');
const errorHeadEl = document.querySelector("#errorHead");
const errorsDivEl = document.querySelector('#errors');
const msgFromServerEl = document.querySelector('#msgFromServer');

importCheckboxEl.addEventListener('change', showChecked);
exportCheckboxEl.addEventListener('change', showChecked);
window.addEventListener('DOMContentLoaded',showErrorFromServer);

//importFormEl.addEventListener('submit', importValidation);


function showChecked(){

    //showError(errorMsgEl,NO_ERROR);
    //showError(msgFromServerEl, NO_ERROR);
    errorsDivEl.style.display = 'none';
    showError(NO_ERROR);
    if ((importCheckboxEl.checked && exportCheckboxEl.checked)||(!importCheckboxEl.checked && !exportCheckboxEl.checked)) {
        if (importCheckboxEl.checked) // if both checked
            showError(TWO_CHECK);
        importFormEl.style.display = 'none';
        exportFormEl.style.display = 'none';
    }
    else if (importCheckboxEl.checked){
        importFormEl.style.display = 'block';
        exportFormEl.style.display = 'none';
    }
    else if (exportCheckboxEl.checked){
        importFormEl.style.display = 'none';
        exportFormEl.style.display = 'block';
    }
}

//function submitImportForm(event){
    //event.preventDefault()
    //{
        //const XHR = new XMLHttpRequest();
       // const FD = new FormData( this );
        //XHR.open( "POST", "/data/import" );
       // XHR.enctype = "multipart/form-data";
       // XHR.send( FD );
  //  }
  //  }

//}

function showError(errorMsg){
    if (errorMsg === NO_ERROR) {
        errorMsgEl.textContent = '';
        errorHeadEl.textContent = '';
        let errorLines= errorsDivEl.getElementsByTagName('p').length;
        for (let i = errorLines-1; i >= 0 ; i--){
            errorsDivEl.removeChild(errorsDivEl.getElementsByTagName('p')[i]);
        }
    }
    else if (errorMsg === TWO_CHECK) {
        errorHeadEl.textContent = TWO_CHECK;
        errorsDivEl.style.display = 'block';

    }
    else
    {
        errorsDivEl.style.display = 'block';
        errorHeadEl.textContent = ERROR_HEAD;
        let i =0;
        let j =0;

        while ((j = errorMsg.indexOf('\n', i)) !== -1) {
            createErrorLines(errorMsg.substring(i, j));
            i = j + 1;
        }
    }
}

function createErrorLines(line){
    let newLineEl = document.createElement('p');
    errorsDivEl.appendChild(newLineEl);
    newLineEl.textContent = line;
    newLineEl.style.color = "red";
}

async function showErrorFromServer(){
    const response = await fetch('/data/import', {
        method: 'get'})
    const result = await response.text();
    if (result == "success")
        msgFromServerEl.textContent = "Data Imported Successfully";
    else{
        showError(result);
    }

}