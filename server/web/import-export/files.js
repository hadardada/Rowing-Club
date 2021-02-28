const NO_ERROR = '';
const TWO_CHECK= 'You must pick one action at a time'

const importCheckboxEl = document.querySelector('#import');
const exportCheckboxEl = document.querySelector('#export');
const importFormEl = document.querySelector('#importForm');
const exportFormEl = document.querySelector('#exportForm');
const errorMsgEl = document.querySelector('#errorChoice');

importCheckboxEl.addEventListener('change', showChecked);
exportCheckboxEl.addEventListener('change', showChecked);
window.addEventListener('DOMContentLoaded',showErrorFromServer);

//importFormEl.addEventListener('submit', submitImportForm);


function showChecked(){
    showError(errorMsgEl,NO_ERROR);
    if ((importCheckboxEl.checked && exportCheckboxEl.checked)||(!importCheckboxEl.checked && !exportCheckboxEl.checked)) {
        if (importCheckboxEl.checked) // if both checked
            showError(errorMsgEl,TWO_CHECK);
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

function showError(whereEl, errorMsg){
    if (errorMsg === NO_ERROR)
        whereEl.textContent = '';
    else
        whereEl.textContent = "Error! "+ errorMsg;
}

async function showErrorFromServer(){
    const response = await fetch('/data/import', {
        method: 'get'})
    const result = await response.text();
    errorMsgEl.textContent = result;

}