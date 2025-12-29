async function assignQuesSetter(event, committeeId){
    event.preventDefault();

    const courseId = document.getElementById("course").value;
    const internalTeacherId = document.getElementById("internal").value;
    const externalTeacherId = document.getElementById("external").value;

    const submitBtn = document.getElementById("assign-btn");
    if(submitBtn) submitBtn.disabled = true;

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
        if(submitBtn) submitBtn.disabled = false;
    }

}