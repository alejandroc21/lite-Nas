//just a temporal solution too
function uploadFiles(){
    const fileInput = document.getElementById('fileInput');
    const files = fileInput.files;

    if(files.length>0){
        const formData = new FormData();

        for(let i=0; i<files.length; i++){
            formData.append('files', files[i]);
        }
        sendFiles(formData);
    }else{
        console.error('No files selected.');
    }
}

function sendFiles(formData){
    fetch('/media/multiple', {
        method:'POST',
        body: formData
    }).then(response=>response.json())
    .then(data=>{
        console.log('link: ', data.urls);
        alert('link: ', data.urls);
    }).catch(error=>console.error(error));
}