async function assignQuesSetter(event, committeeId){
    event.preventDefault();

    const courseId = document.getElementById("course").value;
    const internalTeacherId = document.getElementById("internal").value;
    const externalTeacherId = document.getElementById("external").value;

    const submitBtn = document.getElementById("assign-btn");
    if(submitBtn){
        submitBtn.disabled = true;
        submitBtn.innerText = "Assigning..."
    }

    if(!courseId || !internalTeacherId || !externalTeacherId){
        alert("Provide all necessary data.");
        return;
    }

    try{

        const response = await fetch('/committee/api/assign-setter', {
            method: 'POST',
            headers: {'Content-Type' : 'application/json'},
            body: JSON.stringify({committeeId, courseId, internalTeacherId, externalTeacherId})
        });

        const data = await response.json();
        if(response.ok){
            alert(data.message);
            window.location.href = `/committee/manage/${committeeId}`;
        }
        else{
            console.log(data.message);
            alert(data.message);
        }
    }catch (err){
        console.log(err);
        alert("An error occurred.");
    }finally {
        if(submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerText = "Assign";
        }
    }

}

async function updateExamineeCount(element){
    const committeeId = element.getAttribute("data-id");
    console.log(committeeId);
    if(committeeId == null || isNaN(committeeId)){
        alert("Error: Committee ID not found or invalid.");
        return;
    }
    const courseId = document.getElementById("course3").value;
    const examineeCount = document.getElementById("examinee").value;
    if(examineeCount <= 0){
        alert("Error: Number of participated student in examination can not be zero or negative.")
        return;
    }
    if(courseId == null || isNaN(courseId)){
        alert("Error: Course ID not found or invalid, please select a course.");
        return;
    }

    const btn = document.getElementById("examinee-btn");
    try{
        if(btn) {
            btn.disabled = true;
            btn.innerText = "Updating...";
        }

        const response = await fetch('/api/committee/course/update-examinee', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({committeeId, courseId, examineeCount})
        });

        const data = await response.json();
        if(response.ok){
            alert("Success: " + data.message);
            window.location.href = `/committee/manage/${committeeId}`;
        }
        else{
            alert("Error: " + data.message);
        }


    }catch (err){
        console.log(err);
        alert("An error occurred: " + err);
    }finally {
        if (btn) {
            btn.disabled = false;
            btn.innerText = "Update";
        }
    }
}

async function assignThirdExaminer(event, committeeId){
    event.preventDefault();

    const courseId = document.getElementById("course2").value;
    const examinerId = document.getElementById("examiner").value;
    const scriptsCount = document.getElementById("scripts-count").value;

    if(!courseId || !committeeId || !examinerId || !scriptsCount){
        alert("Error: Invalid parameters");
        return;
    }
    if(isNaN(committeeId) || isNaN(courseId) || isNaN(examinerId) || isNaN(scriptsCount)){
        alert("Error: Invalid data type of any given parameter.");
        return;
    }

    const btn = document.getElementById("third-examiner-btn");
    try{
        if(btn) btn.disabled = true;

        const response = await fetch('/api/assign-thirdexaminer', {
            method: 'POST',
            headers: {'Content-Type' : 'application/json'},
            body: JSON.stringify({committeeId, courseId, examinerId, scriptsCount})
        });

        const data = await response.json();
        if(response.ok){
            alert(data.message);
            window.location.href = `/committee/manage/${committeeId}`;
        }
        else{
            alert(data.message);
        }

    }catch (err){

    }finally {
        if(btn) btn.disabled = false;
    }
}