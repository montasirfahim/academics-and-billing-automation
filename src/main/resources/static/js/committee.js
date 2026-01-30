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
            method: 'PUT',
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

async function  editStudentCount(event, committeeId){
    event.preventDefault();
    const newValue = document.getElementById("student").value;
    if(!newValue || isNaN(newValue)){
        alert("Please provide valid integer number.")
        return;
    }
    try{
        const response = await fetch(`/api/committee/update-student`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({committeeId, newValue})
        });
        const data = await response.json();
        if(response.ok){
            alert("Student count has been updated successfully");
            window.location.href = `/committee/manage/${committeeId}`;
        }
        else{
            alert(data.message);
        }
    }catch (err){
        console.log(err);
        alert("An error occurred!");
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
            method: 'PUT',
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
    const rawStudentsId = document.getElementById("studentsId").value;

    if(!courseId || !committeeId || !examinerId || !rawStudentsId){
        alert("Error: Invalid parameters");
        return;
    }
    if(isNaN(committeeId) || isNaN(courseId) || isNaN(examinerId)){
        alert("Error: Invalid data type of any given parameter.");
        return;
    }

    const btn = document.getElementById("third-examiner-btn");
    try{
        if(btn) btn.disabled = true;

        const response = await fetch('/api/assign-thirdexaminer', {
            method: 'POST',
            headers: {'Content-Type' : 'application/json'},
            body: JSON.stringify({committeeId, courseId, examinerId, rawStudentsId})
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

async function callModeration(element) {
    const id = element.getAttribute("data-id");

    const now = new Date().toLocaleString("en-US", {
        timeZone: "Asia/Dhaka",
        hour12: true,             // AM/PM
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit"
    });
    const callTime = now.replace(", ", ", ");
    console.log("Call Time:", callTime);

    const meetingTime = document.getElementById("meeting-date").value; // YYYY-MM-DDTHH:mm format
    if(!meetingTime) {
        alert("Please select a meeting date and time");
        return;
    }

    try{
        const dateObj = new Date(meetingTime);
        const datePart = dateObj.toLocaleDateString('en-GB', {
            timeZone: "Asia/Dhaka",
            year: "numeric",
            month: "2-digit",
            day: "2-digit"
        }).replace(/\//g, '-');

        //Using 'en-US' ensures the time uses the 12-hour format with AM/PM.
        const timePart = dateObj.toLocaleTimeString('en-US', {
            timeZone: "Asia/Dhaka",
            hour12: true,
            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit"
        });

        const formattedMeetingTime = `${datePart}, ${timePart}`;
        console.log(formattedMeetingTime);

        const payload = {
            callTime: callTime,
            meetingTime: formattedMeetingTime,
            dateObj: dateObj
        };

        const response = await fetch(`/committee/api/moderation/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })

        const message = await response.text();
        if(response.ok){
            alert(message);
            window.location.href = `/committee/manage/${id}`;
        }
        else if (response.status === 401) {
            window.location.href = "/login";
        }
        else{
            alert(message);
        }

    } catch(err){
        console.error("Error:", err);
        alert("Something went wrong!: " + err);
    }
}

async function markResultPublished(element){
    const rawId = element.dataset.id;
    console.log(rawId);
    if(!rawId || isNaN(rawId)){
        alert("Committee ID not found or invalid data type!");
        return;
    }
    element.disabled = true;

    try{
        const committeeId = parseInt(rawId, 10);
        const response = await fetch(`/api/committee/publish-result/${committeeId}`, {
            method: 'PUT',
            headers: { "Content-Type": "application/json" }
        });

        const data = await response.json();
        if(response.ok){
            alert(data.message);
            window.location.href = `/committee/manage/${committeeId}`;
        }
        else if(response.status === 401){
            alert(data.message);
            window.location.href = "/login";
        }
        else{
            alert(data.message);
            element.disabled = false;
        }
    }catch (err){
        alert("Something went wrong: " + err);
        console.log(err);
        element.disabled = false;
    }
}


function addNewSupervisorRow() {
    const container = document.getElementById("details-container");
    const firstRow = document.querySelector(".details-row");

    //cloning first row
    const newRow = firstRow.cloneNode(true);

    //cleaning values in the cloned row
    newRow.querySelectorAll('input').forEach(input => input.value = '');
    newRow.querySelectorAll('select').forEach(select => select.selectedIndex = 0);

    container.appendChild(newRow);
}

function deleteSupervisorRow(button){
    const container = document.getElementById("details-container");

    if(container.querySelectorAll('.details-row').length > 1){
        button.closest('.details-row').remove();
    }
    else{
        alert("You must have at least one supervisor row.");
    }
}

async function assignSupervisors(event){
    event.preventDefault();

    const courseId = document.getElementById("thesis-course").value;
    console.log("course id: " + courseId);

    const committeeId = event.currentTarget.dataset.committeeId;

    if(!courseId || isNaN(courseId)){
        alert("Invalid course ID");
        return;
    }
    if(!committeeId || isNaN(committeeId)){
        alert("Did not find valid committee ID");
        return;
    }
    const superVisorRows = document.querySelectorAll('.details-row');
    const superVisionData = [];
    let totalStudent = 0;

    superVisorRows.forEach(row => {
        const internalId = row.querySelector('select').value;
        const groupCount = row.querySelector('input[placeholder*="groups"]').value;
        const studentCount = row.querySelector('input[placeholder*="students"]').value;
        totalStudent += parseInt(studentCount);

        if(internalId && groupCount && studentCount){
            superVisionData.push({
                teacherId: parseInt(internalId),
                numberOfGroups: parseInt(groupCount),
                numberOfStudents: parseInt(studentCount)
            });
        }
    });

    if(superVisionData.length === 0) {
        alert("Please add at least one supervisor entry.");
        return;
    }

    console.log("sending data: ", superVisionData);
    try{
        const response = await fetch('/api/thesis-project/assign-supervisors', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({superVisionData, courseId, committeeId, totalStudent})
        });

        const data = await response.json();
        if(response.ok){
            alert("Supervisors has been assigned successfully for the selected course!");
        }
        else{
            alert(data.message);
        }
    } catch(err){
        alert("Connection failed: " + err);
    }


}