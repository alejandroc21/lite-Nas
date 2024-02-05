const main = document.querySelector('main');
const filesPage = document.querySelector('.files');
const uploadFiles = document.querySelector('.upload-files');
const container = document.querySelector('.container');
const preview = document.querySelector('#preview');
let folderFiles;
let data = [];

async function listFiles(folder) {
    folderFiles = folder;
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

        card.onclick = function () {
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

/* Changed fetch method for XMLHttpRequest because cause fetch does not
 * provide a progress event */

async function uploadFile(file) {
    const formData = new FormData();
    formData.append('file', file);

    const cardFile = document.createElement('div');
    cardFile.className = 'card-file';
    const fileName = document.createElement('h3');
    fileName.textContent = file.name;
    const fileState = document.createElement('span');
    cardFile.append(fileName);
    preview.append(cardFile);

    const progressBar = document.createElement('progress');
    progressBar.value = 0;
    progressBar.max = 100;
    preview.append(progressBar);

    const xhr = new XMLHttpRequest();

    xhr.upload.addEventListener('progress', function (event) {
        if (event.lengthComputable) {
            const percentComplete = (event.loaded / event.total) * 100;
            progressBar.value = percentComplete;
        }
    });

    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                fileState.textContent = "Upload success";
                fileState.classList.add('success');

            } else if (xhr.status === 413) {
                const message = JSON.parse(xhr.responseText);
                fileState.textContent = message.ERROR;
                fileState.classList.add('failure');
                
            } else {
                fileState.textContent = "Fail to Upload";
                fileState.classList.add('failure');
            }
            progressBar.remove();
            cardFile.append(fileState);
        }
    };

    xhr.open('POST', '/media/upload');
    xhr.send(formData);
}

const modal = document.querySelector('#modal');
const fileContent = document.querySelector('#file-content');
const description = document.querySelector('#description');
const options = description.querySelector('#options');

function openModal(id) {
    modal.showModal();
    modal.style.display = 'flex';
    showDataFile(id);
}

async function showDataFile(id) {
    const fileURL = data[id].url;
    fileContent.innerHTML = '';

    try {
        const res = await fetch(fileURL);
        const contentType = res.headers.get('content-Type');

        if (contentType.startsWith('image/')) {
            const image = document.createElement('img');
            image.src = fileURL;
            fileContent.append(image);

        } else if (contentType.startsWith('video/')) {
            const video = document.createElement("video");
            video.setAttribute("controls", "true");

            const source = document.createElement("source");
            source.setAttribute("src", fileURL);
            source.setAttribute("type", contentType);

            video.appendChild(source);
            fileContent.append(video);

        } else if (contentType.startsWith('audio/')) {
            const audio = document.createElement("audio");
            audio.setAttribute("controls", "true");

            const source = document.createElement("source");
            source.setAttribute("src", fileURL);
            source.setAttribute("type", contentType);

            const image = document.createElement('img');
            image.src = data[id].logo;
            image.className = "img-file";
            fileContent.append(image);

            audio.appendChild(source);
            fileContent.append(audio);

        } else {
            const image = document.createElement('img');
            image.src = data[id].logo;
            image.className = "img-file";
            fileContent.append(image);
        }

        const name = description.querySelector('#detail-name');
        const date = description.querySelector('#detail-date');
        const size = description.querySelector('#detail-size');
        const fileDate = new Date(data[id].creationDate);

        name.textContent = data[id].name;
        date.textContent = fileDate.toLocaleString();
        size.textContent = bytesToSize(data[id].bytesSize);

        const downloadButton = options.querySelector('#btn-download');
        const buttonDelete = options.querySelector('#btn-delete');

        downloadButton.onclick = function () {
            downloadFile(id);
        }

        buttonDelete.onclick = function () {
            openModalDelete(id);
        }
    } catch (error) {
        console.error(error);
    }
}

window.addEventListener('click', (event) => {
    if (event.target === modal) {
        closeModal();
    }
});

window.addEventListener('keydown', (event) => {
    if (event.key === 'Escape') {
        closeModal();
    }
});

function closeModal() {
    modal.close();
    modal.style.display = 'none';
    fileContent.innerHTML = '';
}

function bytesToSize(bytes) {
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    if (bytes === 0) return 'n/a';
    const i = parseInt(Math.floor(Math.log(Math.abs(bytes)) / Math.log(1024)), 10);
    if (i === 0) return `${bytes} ${sizes[i]}`;
    return `${(bytes / (1024 ** i)).toFixed(2)} ${sizes[i]}`;
}

function downloadFile(id) {
    fetch(data[id].url).then(response => response.blob())
        .then(blob => {
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = data[id].name;
            document.body.appendChild(a);
            a.click();
            URL.revokeObjectURL(url);

        }).catch(error => console.error(error));
}

const modalDelete = document.querySelector('#modal-delete');
const btnYes = modalDelete.querySelector('#btn-yes');
const btnNot = modalDelete.querySelector('#btn-not');

function openModalDelete(id) {
    btnYes.onclick = function () {
        confirmDelete(id);
    }
    modalDelete.showModal();
}

function confirmDelete(id) {
    fetch(data[id].url, {
        method: 'DELETE',
    })
        .then(response => response.text())
        .then(res => {
            console.log(res);
            modalDelete.close();
            closeModal();
        })
        .catch(error => console.error(error));
    listFiles(folderFiles);
}

btnNot.addEventListener('click', (e) => {
    modalDelete.close();
});

window.addEventListener('click', (event) => {
    if (event.target === modalDelete) {
        modalDelete.close();
    }
});