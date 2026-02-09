async function  updateBillRate(event, id){
    event.preventDefault();
    const newRate = document.getElementById("new-rate").value;
    if(!newRate || newRate <= 0){
        alert("Please enter positive numeric value as bill rate")
        return;
    }
    console.log(newRate)
    try{
        const response = await fetch(`/api/update-bill-rate`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({id, newRate})
        });
        const data = await response.json();
        if(response.ok){
            alert("Bill Rate has been updated successfully");
            window.location.href = `/dashboard`;
        }
        else{
            alert(data.message);
        }
    }catch (err){
        console.log(err);
        alert("An error occurred!");
    }
}

async function  editGrade(event, userId){
    event.preventDefault();
    const newSalaryGrade = document.getElementById("salaryGrade").value;
    if(!newSalaryGrade){
        alert("Please select new salary grade!")
        return;
    }
    console.log("clicked");
    try{
        const response = await fetch(`/api/user/update/salary-grade/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({newSalaryGrade})
        });
        const data = await response.json();
        if(response.ok){
            alert(data.message);
            window.location.href = `/user/profile/${userId}`;
        }
        else if(response.status === 401){
            alert(data.message);
            window.location.href = "/login";
        }
        else{
            alert(data.message);
        }
    }catch (err){
        console.log(err);
        alert("An error occurred!");
    }
}