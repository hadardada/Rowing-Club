import { BoatJson } from './addNew.js'

const UPDATE_NAME = 1;
const UPDATE_STATUS = 2;
const UPDATE_PRIVATE = 3;
const UPDATE_OARS = 4;
const UPDATE_COXSWAIN = 5;
const UPDATE_COASTAL = 6;

const boatSerialNumber = URLSearchParams.get('serialNum');

const updateOarsButtonEl = document.querySelector('updateOars');
const updateOarsErrorEl = document.querySelector('#oarsError');

updateOarsButtonEl.addEventListener('DOMContentLoaded',injectParameters)

function injectParameters(){

}
