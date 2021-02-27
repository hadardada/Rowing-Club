const NO_ERROR = '';
const TWO_CHECK= 'You must pick one action at a time'

const importCheckboxEl = document.querySelector('#import');
const exportCheckboxEl = document.querySelector('#export');
const importFormEl = document.querySelector('#importForm');
const exportFormEl = document.querySelector('#exportForm');


importCheckboxEl.addEventListener('change', showChecked);
exportCheckboxEl.addEventListener('change', showChecked);


function showChecked(){
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

function showError(whereEl, errorMsg){
    if (errorMsg === NO_ERROR)
        whereEl.textContent = '';
    else
        whereEl.textContent = "Error! "+ errorMsg;
}