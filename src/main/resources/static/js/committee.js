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