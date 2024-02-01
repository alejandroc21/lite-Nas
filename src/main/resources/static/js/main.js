const main = document.querySelector('main');
const filesPage = document.querySelector('.files');
const uploadFiles = document.querySelector('.upload-files');
const container = document.querySelector('.container');
const preview = document.querySelector('#preview');
let data = [];

async function listFiles(folder) {
    const res = await fetch("media/list/" + folder);
    data = await res.json();
    container.style.display = 'block';
    filesPage.innerHTML = '';
    preview.innerHTML = '';
    uploadFiles.style.display = 'none';

    const header = document.querySelector('.header');
    header.textContent = folder.toUpperCase();

    data.forEach((element, index) => {

        const card = document.createElement('div');
        card.className = 'card';
        card.id = index;

        card.onclick = function(){
            openModal(this.id);
        }

        const img = document.createElement('img');
        img.src = element.logo;

        const title = document.createElement('h3');
        title.textContent = element.name;

        card.append(img, title);
        filesPage.append(card);
    });
}

function showUpload() {
    container.style.display = 'none';
    uploadFiles.style.display = 'block';
}


const dropArea = document.querySelector('.drop-area');
const dragArea = dropArea.querySelector('h2');
const button = dropArea.querySelector('button');
const input = dropArea.querySelector('#input-file');
let files;
let fileArray = [];

button.addEventListener('click', (e) => {
    input.click();
});

input.addEventListener('change', (e) => {
    files = input.files;
    dropArea.classList.add('active');
    showFiles(files);
    dropArea.classList.remove('active');
});

dropArea.addEventListener('dragover', (e) => {
    e.preventDefault();
    dropArea.classList.add('active');
});

dropArea.addEventListener('dragleave', (e) => {
    e.preventDefault();
    dropArea.classList.remove('active');
});

dropArea.addEventListener('drop', (e) => {
    e.preventDefault();
    dropArea.classList.remove('active');
    files = e.dataTransfer.files;
    showFiles(files);
});


function showFiles(files) {
    for (const file of files) {
        uploadFile(file);
    }
}

async function uploadFile(file) {
    const formData = new FormData();
    formData.append('file', file);

    const cardFile = document.createElement('div');
    cardFile.className = 'card-file';
    const fileName = document.createElement('h3');
    fileName.textContent = file.name;
    const fileState = document.createElement('span');

    try {
        const response = await fetch("/media/upload", {
            method: 'POST',
            body: formData,
        });
        if(response.status===413){
            const message = await response.json();
            fileState.textContent = message.ERROR;    
            fileState.classList.add('failure');
        }else{
            fileState.textContent = "Upload success";
            fileState.classList.add('success');
        }

    } catch {
        fileState.textContent = "Fail to Upload";
        fileState.classList.add('failure');
    }

    cardFile.append(fileName, fileState);
    preview.append(cardFile);
}

function prueba(id){
    console.log(id);
}

const modal = document.querySelector('#modal');
const nameModal = modal.querySelector('h2');


function openModal(id){
    modal.showModal();
    console.log(data[id]);
    nameModal.textContent = data[id].name;
    const image = modal.createElement('img');
    image.src = data[id].url;
}

function closeModal(){
    modal.close();
}