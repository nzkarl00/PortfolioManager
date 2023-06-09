
setTimeout(() => {dateTooltip = '<span id="info" class="tooltip_body_evidence">This is the date the evidence occurred, date selection is restricted to the boundaries of the project it is assigned to.</span>'

                  $("#evidence_date_tool").tooltip({
                      title: dateTooltip,
                      placement: "top",
                      trigger: "hover",
                      container: "body",
                      html: true
                  });}, 20)

categoryOptionsShown = true;
selectedCategories = new Set()

function storeCategories() {
    let categoryStore = document.getElementById("category_hidden")
    const categoriesToSave = Array.from(selectedCategories).join('~');
    categoryStore.value = categoriesToSave
}

function showCategories() {
    let categoryOptions = document.getElementById("category_multiselect_items")
    if (categoryOptionsShown) {
        categoryOptions.style.display = "block"
        categoryOptionsShown = false
    } else {
        categoryOptions.style.display = "none"
        categoryOptionsShown = true
    }
}

function updateCategories(event) {
    let newCategory = event.value
    if (selectedCategories.has(newCategory)) {
        selectedCategories.delete(newCategory)
    } else {
        selectedCategories.add(event.value)
    }
    let categoryDisplay = document.getElementById("evidence_category")
    if (selectedCategories.size == 0) {
        categoryDisplay.innerText = "Select Categories"
    } else {
        categoryDisplay.innerText = [...selectedCategories].join(', ')
    }
    storeCategories()
}

document.addEventListener("click", function (e) {
    let categoryOptions = document.getElementById("category_multiselect_items")
    if (e.target != document.getElementById("evidence_category") && e.target.className != "category_item" && categoryOptions != null) {
        categoryOptions.style.display = "none"
        categoryOptionsShown = true
    }
});

// Start of Users
//
//
//


// Event listener to add user when enter is pressed and the user input is selected
setTimeout(() => {document.querySelector("#add_user_input").addEventListener("keyup", event => {
                      if (event.key !== "Enter" && event.key !== ".") return;
                      addUser()
                  })}, 20)


//Returns the users
function storeUsers() {
    let userStore = document.getElementById("userHidden")
    const userList = Array.from(users).join('~');
    userStore.value = userList
}

users = new Set()
userRow = document.getElementById("user_sub_1");
setTimeout(() => {userRow = document.getElementById("user_sub_1");}, 10)


// Adds the user to the set and resets the input.
function addUser() {
    let newUser = document.getElementById("add_user_input").value
    if (newUser.slice(-1) === " ") {
        newUser = newUser.slice(0, -1);
    }

    if (users.has(newUser)) {
        document.getElementById("add_user_input").value = "";
        return;
    }

    if (newUser == "") {
        document.getElementById("user_error").style = "display:none;";
        return;
    }

    // check to see if it matches the correct user format
    const userInAllUsers = allUsers.includes(newUser);
    if (!userInAllUsers) {
        UsernameOrId = false;
        // try matching for sole id or username
        for (const user of allUsers) {
            username = user.split(":")[1]
            id = user.split(":")[0]
            if (newUser.toLowerCase() == username.toLowerCase() || newUser == id) {
                newUser = user
                UsernameOrId = true
                break
            }
        }
        if (!UsernameOrId) {
            document.getElementById("user_error").style = "color:red;";
            return
        }
    } else {
        document.getElementById("user_error").style = "display:none;";
    }
    document.getElementById("add_user_input").value = ""
    appendUser(newUser, false)
}

author = "";

// Inserts a new user tag into the form
function appendUser(userText, isAuthor) {

    users.add(userText)
    allUsers = allUsers.filter(s => s !== userText)
    autocomplete(document.getElementById("add_user_input"), allUsers, "user")

    username = userText.split(":")[1]
    id = userText.split(":")[0]
    let userTag;
    if (isAuthor) {
        author = userText;
        userTag = document.createElement("p");
        userTag.className = "table_text author_tag"
        userTag.innerHTML = '<img class="img-fluid rounded-circle rounded-circle" style="margin-right: 0.5em;" src="' + imagePrefix + id + '" width=35em height=35em alt="">' + username
    } else {
        userTag = document.createElement("button");
        userTag.className = "table_text contributor_tag"
        userTag.onclick = removeUser;
        userTag.innerHTML = '<img class="img-fluid rounded-circle rounded-circle" style="margin-right: 0.5em;" src="' + imagePrefix + id + '" width=35em height=35em alt="">' + username + " ✖"
    }
    userTag.id = "user_" + userText
    userTag.value = userText
    userRow.appendChild(userTag);
    if (userRow.getBoundingClientRect().right <= userTag.getBoundingClientRect().right) {
        userRow.removeChild(userTag)
        insertUserRow()
        userRow.appendChild(userTag);
    }
    storeUsers()
}

/* Creates a new row for user tags to be placed on, for some reason inserting <br> couldn't do this*/
function insertUserRow() {
    let newUserRow = document.createElement("div");
    newUserRow.style.display = "flex"
    document.getElementById("users_container").appendChild(newUserRow)
    userRow = newUserRow
}

// Deletes a user then rebuilds the user list
function removeUser(event) {
    users.delete(event.target.value)
    allUsers.push(event.target.value)
    autocomplete(document.getElementById("add_user_input"), allUsers, "user")
    clearUsers()
    insertUserRow()
    for (const user of users) {
        if (user == author) {
            appendUser(user, true)
        } else {
            appendUser(user, false)
        }
    }
    return false;
}

// Removes all user from display
function clearUsers() {
    let userContainer = document.getElementById("users_container")
    while (userContainer.lastChild) {
        userContainer.removeChild(userContainer.lastChild)
    }
}


// Start of Skills
//
//
//

// Event listener to add skills when enter is pressed and the skill input is selected
setTimeout(() => {document.querySelector("#add_skill_input").addEventListener("keyup", event => {
    if (event.key === "Enter" || event.key === " ") {
        addSkill()
    } else if (event.key === "Delete") {
        skillRow.children.item(skillRow.children.length-1).click() // Negative index doesn't work for htmlcollection
    }
})}, 20)


//Returns the skills
function storeSkills() {
    let skillStore = document.getElementById("skillHidden")
    const skillList = Array.from(skills).join('~');
    skillStore.value = skillList
}

skills = new Set()
skillRow = document.getElementById("skill_sub_1");
skillCharLimit = 50;
emojiPattern = /^(\u00a9|\u00ae|[\u2000-\u3300]|\ud83c[\ud000-\udfff]|\ud83d[\ud000-\udfff]|\ud83e[\ud000-\udfff]){2,}$/
allChrPattern = /([\u0041-\u005A\u0061-\u007A\u00AA\u00B5\u00BA\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02C1\u02C6-\u02D1\u02E0-\u02E4\u02EC\u02EE\u0370-\u0374\u0376\u0377\u037A-\u037D\u0386\u0388-\u038A\u038C\u038E-\u03A1\u03A3-\u03F5\u03F7-\u0481\u048A-\u0527\u0531-\u0556\u0559\u0561-\u0587\u05D0-\u05EA\u05F0-\u05F2\u0620-\u064A\u066E\u066F\u0671-\u06D3\u06D5\u06E5\u06E6\u06EE\u06EF\u06FA-\u06FC\u06FF\u0710\u0712-\u072F\u074D-\u07A5\u07B1\u07CA-\u07EA\u07F4\u07F5\u07FA\u0800-\u0815\u081A\u0824\u0828\u0840-\u0858\u08A0\u08A2-\u08AC\u0904-\u0939\u093D\u0950\u0958-\u0961\u0971-\u0977\u0979-\u097F\u0985-\u098C\u098F\u0990\u0993-\u09A8\u09AA-\u09B0\u09B2\u09B6-\u09B9\u09BD\u09CE\u09DC\u09DD\u09DF-\u09E1\u09F0\u09F1\u0A05-\u0A0A\u0A0F\u0A10\u0A13-\u0A28\u0A2A-\u0A30\u0A32\u0A33\u0A35\u0A36\u0A38\u0A39\u0A59-\u0A5C\u0A5E\u0A72-\u0A74\u0A85-\u0A8D\u0A8F-\u0A91\u0A93-\u0AA8\u0AAA-\u0AB0\u0AB2\u0AB3\u0AB5-\u0AB9\u0ABD\u0AD0\u0AE0\u0AE1\u0B05-\u0B0C\u0B0F\u0B10\u0B13-\u0B28\u0B2A-\u0B30\u0B32\u0B33\u0B35-\u0B39\u0B3D\u0B5C\u0B5D\u0B5F-\u0B61\u0B71\u0B83\u0B85-\u0B8A\u0B8E-\u0B90\u0B92-\u0B95\u0B99\u0B9A\u0B9C\u0B9E\u0B9F\u0BA3\u0BA4\u0BA8-\u0BAA\u0BAE-\u0BB9\u0BD0\u0C05-\u0C0C\u0C0E-\u0C10\u0C12-\u0C28\u0C2A-\u0C33\u0C35-\u0C39\u0C3D\u0C58\u0C59\u0C60\u0C61\u0C85-\u0C8C\u0C8E-\u0C90\u0C92-\u0CA8\u0CAA-\u0CB3\u0CB5-\u0CB9\u0CBD\u0CDE\u0CE0\u0CE1\u0CF1\u0CF2\u0D05-\u0D0C\u0D0E-\u0D10\u0D12-\u0D3A\u0D3D\u0D4E\u0D60\u0D61\u0D7A-\u0D7F\u0D85-\u0D96\u0D9A-\u0DB1\u0DB3-\u0DBB\u0DBD\u0DC0-\u0DC6\u0E01-\u0E30\u0E32\u0E33\u0E40-\u0E46\u0E81\u0E82\u0E84\u0E87\u0E88\u0E8A\u0E8D\u0E94-\u0E97\u0E99-\u0E9F\u0EA1-\u0EA3\u0EA5\u0EA7\u0EAA\u0EAB\u0EAD-\u0EB0\u0EB2\u0EB3\u0EBD\u0EC0-\u0EC4\u0EC6\u0EDC-\u0EDF\u0F00\u0F40-\u0F47\u0F49-\u0F6C\u0F88-\u0F8C\u1000-\u102A\u103F\u1050-\u1055\u105A-\u105D\u1061\u1065\u1066\u106E-\u1070\u1075-\u1081\u108E\u10A0-\u10C5\u10C7\u10CD\u10D0-\u10FA\u10FC-\u1248\u124A-\u124D\u1250-\u1256\u1258\u125A-\u125D\u1260-\u1288\u128A-\u128D\u1290-\u12B0\u12B2-\u12B5\u12B8-\u12BE\u12C0\u12C2-\u12C5\u12C8-\u12D6\u12D8-\u1310\u1312-\u1315\u1318-\u135A\u1380-\u138F\u13A0-\u13F4\u1401-\u166C\u166F-\u167F\u1681-\u169A\u16A0-\u16EA\u1700-\u170C\u170E-\u1711\u1720-\u1731\u1740-\u1751\u1760-\u176C\u176E-\u1770\u1780-\u17B3\u17D7\u17DC\u1820-\u1877\u1880-\u18A8\u18AA\u18B0-\u18F5\u1900-\u191C\u1950-\u196D\u1970-\u1974\u1980-\u19AB\u19C1-\u19C7\u1A00-\u1A16\u1A20-\u1A54\u1AA7\u1B05-\u1B33\u1B45-\u1B4B\u1B83-\u1BA0\u1BAE\u1BAF\u1BBA-\u1BE5\u1C00-\u1C23\u1C4D-\u1C4F\u1C5A-\u1C7D\u1CE9-\u1CEC\u1CEE-\u1CF1\u1CF5\u1CF6\u1D00-\u1DBF\u1E00-\u1F15\u1F18-\u1F1D\u1F20-\u1F45\u1F48-\u1F4D\u1F50-\u1F57\u1F59\u1F5B\u1F5D\u1F5F-\u1F7D\u1F80-\u1FB4\u1FB6-\u1FBC\u1FBE\u1FC2-\u1FC4\u1FC6-\u1FCC\u1FD0-\u1FD3\u1FD6-\u1FDB\u1FE0-\u1FEC\u1FF2-\u1FF4\u1FF6-\u1FFC\u2071\u207F\u2090-\u209C\u2102\u2107\u210A-\u2113\u2115\u2119-\u211D\u2124\u2126\u2128\u212A-\u212D\u212F-\u2139\u213C-\u213F\u2145-\u2149\u214E\u2183\u2184\u2C00-\u2C2E\u2C30-\u2C5E\u2C60-\u2CE4\u2CEB-\u2CEE\u2CF2\u2CF3\u2D00-\u2D25\u2D27\u2D2D\u2D30-\u2D67\u2D6F\u2D80-\u2D96\u2DA0-\u2DA6\u2DA8-\u2DAE\u2DB0-\u2DB6\u2DB8-\u2DBE\u2DC0-\u2DC6\u2DC8-\u2DCE\u2DD0-\u2DD6\u2DD8-\u2DDE\u2E2F\u3005\u3006\u3031-\u3035\u303B\u303C\u3041-\u3096\u309D-\u309F\u30A1-\u30FA\u30FC-\u30FF\u3105-\u312D\u3131-\u318E\u31A0-\u31BA\u31F0-\u31FF\u3400-\u4DB5\u4E00-\u9FCC\uA000-\uA48C\uA4D0-\uA4FD\uA500-\uA60C\uA610-\uA61F\uA62A\uA62B\uA640-\uA66E\uA67F-\uA697\uA6A0-\uA6E5\uA717-\uA71F\uA722-\uA788\uA78B-\uA78E\uA790-\uA793\uA7A0-\uA7AA\uA7F8-\uA801\uA803-\uA805\uA807-\uA80A\uA80C-\uA822\uA840-\uA873\uA882-\uA8B3\uA8F2-\uA8F7\uA8FB\uA90A-\uA925\uA930-\uA946\uA960-\uA97C\uA984-\uA9B2\uA9CF\uAA00-\uAA28\uAA40-\uAA42\uAA44-\uAA4B\uAA60-\uAA76\uAA7A\uAA80-\uAAAF\uAAB1\uAAB5\uAAB6\uAAB9-\uAABD\uAAC0\uAAC2\uAADB-\uAADD\uAAE0-\uAAEA\uAAF2-\uAAF4\uAB01-\uAB06\uAB09-\uAB0E\uAB11-\uAB16\uAB20-\uAB26\uAB28-\uAB2E\uABC0-\uABE2\uAC00-\uD7A3\uD7B0-\uD7C6\uD7CB-\uD7FB\uF900-\uFA6D\uFA70-\uFAD9\uFB00-\uFB06\uFB13-\uFB17\uFB1D\uFB1F-\uFB28\uFB2A-\uFB36\uFB38-\uFB3C\uFB3E\uFB40\uFB41\uFB43\uFB44\uFB46-\uFBB1\uFBD3-\uFD3D\uFD50-\uFD8F\uFD92-\uFDC7\uFDF0-\uFDFB\uFE70-\uFE74\uFE76-\uFEFC\uFF21-\uFF3A\uFF41-\uFF5A\uFF66-\uFFBE\uFFC2-\uFFC7\uFFCA-\uFFCF\uFFD2-\uFFD7\uFFDA-\uFFDC]{2})|(^\u00a9|\u00ae|[\u2000-\u3300]|\ud83c[\ud000-\udfff]|\ud83d[\ud000-\udfff]|\ud83e[\ud000-\udfff]$)/
const skillPattern = /^[\u0041-\u005A\u0061-\u007A\u00AA\u00B5\u00BA\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02C1\u02C6-\u02D1\u02E0-\u02E4\u02EC\u02EE\u0370-\u0374\u0376\u0377\u037A-\u037D\u0386\u0388-\u038A\u038C\u038E-\u03A1\u03A3-\u03F5\u03F7-\u0481\u048A-\u0527\u0531-\u0556\u0559\u0561-\u0587\u05D0-\u05EA\u05F0-\u05F2\u0600-\u0669\u0671-\u06D3\u06D5\u06E5\u06E6\u06EE\u06EF\u06FA-\u06FC\u06FF\u0710\u0712-\u072F\u074D-\u07A5\u07B1\u07CA-\u07EA\u07F4\u07F5\u07FA\u0800-\u0815\u081A\u0824\u0828\u0840-\u0858\u08A0\u08A2-\u08AC\u0904-\u0939\u093D\u0950\u0958-\u0961\u0971-\u0977\u0979-\u097F\u0985-\u098C\u098F\u0990\u0993-\u09A8\u09AA-\u09B0\u09B2\u09B6-\u09B9\u09BD\u09CE\u09DC\u09DD\u09DF-\u09E1\u09F0\u09F1\u0A05-\u0A0A\u0A0F\u0A10\u0A13-\u0A28\u0A2A-\u0A30\u0A32\u0A33\u0A35\u0A36\u0A38\u0A39\u0A59-\u0A5C\u0A5E\u0A72-\u0A74\u0A85-\u0A8D\u0A8F-\u0A91\u0A93-\u0AA8\u0AAA-\u0AB0\u0AB2\u0AB3\u0AB5-\u0AB9\u0ABD\u0AD0\u0AE0\u0AE1\u0B05-\u0B0C\u0B0F\u0B10\u0B13-\u0B28\u0B2A-\u0B30\u0B32\u0B33\u0B35-\u0B39\u0B3D\u0B5C\u0B5D\u0B5F-\u0B61\u0B71\u0B83\u0B85-\u0B8A\u0B8E-\u0B90\u0B92-\u0B95\u0B99\u0B9A\u0B9C\u0B9E\u0B9F\u0BA3\u0BA4\u0BA8-\u0BAA\u0BAE-\u0BB9\u0BD0\u0C05-\u0C0C\u0C0E-\u0C10\u0C12-\u0C28\u0C2A-\u0C33\u0C35-\u0C39\u0C3D\u0C58\u0C59\u0C60\u0C61\u0C85-\u0C8C\u0C8E-\u0C90\u0C92-\u0CA8\u0CAA-\u0CB3\u0CB5-\u0CB9\u0CBD\u0CDE\u0CE0\u0CE1\u0CF1\u0CF2\u0D05-\u0D0C\u0D0E-\u0D10\u0D12-\u0D3A\u0D3D\u0D4E\u0D60\u0D61\u0D7A-\u0D7F\u0D85-\u0D96\u0D9A-\u0DB1\u0DB3-\u0DBB\u0DBD\u0DC0-\u0DC6\u0E01-\u0E30\u0E32\u0E33\u0E40-\u0E46\u0E81\u0E82\u0E84\u0E87\u0E88\u0E8A\u0E8D\u0E94-\u0E97\u0E99-\u0E9F\u0EA1-\u0EA3\u0EA5\u0EA7\u0EAA\u0EAB\u0EAD-\u0EB0\u0EB2\u0EB3\u0EBD\u0EC0-\u0EC4\u0EC6\u0EDC-\u0EDF\u0F00\u0F40-\u0F47\u0F49-\u0F6C\u0F88-\u0F8C\u1000-\u102A\u103F\u1050-\u1055\u105A-\u105D\u1061\u1065\u1066\u106E-\u1070\u1075-\u1081\u108E\u10A0-\u10C5\u10C7\u10CD\u10D0-\u10FA\u10FC-\u1248\u124A-\u124D\u1250-\u1256\u1258\u125A-\u125D\u1260-\u1288\u128A-\u128D\u1290-\u12B0\u12B2-\u12B5\u12B8-\u12BE\u12C0\u12C2-\u12C5\u12C8-\u12D6\u12D8-\u1310\u1312-\u1315\u1318-\u135A\u1380-\u138F\u13A0-\u13F4\u1401-\u166C\u166F-\u167F\u1681-\u169A\u16A0-\u16EA\u1700-\u170C\u170E-\u1711\u1720-\u1731\u1740-\u1751\u1760-\u176C\u176E-\u1770\u1780-\u17B3\u17D7\u17DC\u1820-\u1877\u1880-\u18A8\u18AA\u18B0-\u18F5\u1900-\u191C\u1950-\u196D\u1970-\u1974\u1980-\u19AB\u19C1-\u19C7\u1A00-\u1A16\u1A20-\u1A54\u1AA7\u1B05-\u1B33\u1B45-\u1B4B\u1B83-\u1BA0\u1BAE\u1BAF\u1BBA-\u1BE5\u1C00-\u1C23\u1C4D-\u1C4F\u1C5A-\u1C7D\u1CE9-\u1CEC\u1CEE-\u1CF1\u1CF5\u1CF6\u1D00-\u1DBF\u1E00-\u1F15\u1F18-\u1F1D\u1F20-\u1F45\u1F48-\u1F4D\u1F50-\u1F57\u1F59\u1F5B\u1F5D\u1F5F-\u1F7D\u1F80-\u1FB4\u1FB6-\u1FBC\u1FBE\u1FC2-\u1FC4\u1FC6-\u1FCC\u1FD0-\u1FD3\u1FD6-\u1FDB\u1FE0-\u1FEC\u1FF2-\u1FF4\u1FF6-\u1FFC\u2071\u207F\u2090-\u209C\u2102\u2107\u210A-\u2113\u2115\u2119-\u211D\u2124\u2126\u2128\u212A-\u212D\u212F-\u2139\u213C-\u213F\u2145-\u2149\u214E\u2183\u2184\u2C00-\u2C2E\u2C30-\u2C5E\u2C60-\u2CE4\u2CEB-\u2CEE\u2CF2\u2CF3\u2D00-\u2D25\u2D27\u2D2D\u2D30-\u2D67\u2D6F\u2D80-\u2D96\u2DA0-\u2DA6\u2DA8-\u2DAE\u2DB0-\u2DB6\u2DB8-\u2DBE\u2DC0-\u2DC6\u2DC8-\u2DCE\u2DD0-\u2DD6\u2DD8-\u2DDE\u2E2F\u3005\u3006\u3031-\u3035\u303B\u303C\u3041-\u3096\u309D-\u309F\u30A1-\u30FA\u30FC-\u30FF\u3105-\u312D\u3131-\u318E\u31A0-\u31BA\u31F0-\u31FF\u3400-\u4DB5\u4E00-\u9FCC\uA000-\uA48C\uA4D0-\uA4FD\uA500-\uA60C\uA610-\uA61F\uA62A\uA62B\uA640-\uA66E\uA67F-\uA697\uA6A0-\uA6E5\uA717-\uA71F\uA722-\uA788\uA78B-\uA78E\uA790-\uA793\uA7A0-\uA7AA\uA7F8-\uA801\uA803-\uA805\uA807-\uA80A\uA80C-\uA822\uA840-\uA873\uA882-\uA8B3\uA8F2-\uA8F7\uA8FB\uA90A-\uA925\uA930-\uA946\uA960-\uA97C\uA984-\uA9B2\uA9CF\uAA00-\uAA28\uAA40-\uAA42\uAA44-\uAA4B\uAA60-\uAA76\uAA7A\uAA80-\uAAAF\uAAB1\uAAB5\uAAB6\uAAB9-\uAABD\uAAC0\uAAC2\uAADB-\uAADD\uAAE0-\uAAEA\uAAF2-\uAAF4\uAB01-\uAB06\uAB09-\uAB0E\uAB11-\uAB16\uAB20-\uAB26\uAB28-\uAB2E\uABC0-\uABE2\uAC00-\uD7A3\uD7B0-\uD7C6\uD7CB-\uD7FB\uF900-\uFA6D\uFA70-\uFAD9\uFB00-\uFB06\uFB13-\uFB17\uFB1D\uFB1F-\uFB28\uFB2A-\uFB36\uFB38-\uFB3C\uFB3E\uFB40\uFB41\uFB43\uFB44\uFB46-\uFBB1\uFBD3-\uFD3D\uFD50-\uFD8F\uFD92-\uFDC7\uFDF0-\uFDFB\uFE70-\uFE74\uFE76-\uFEFC\uFF21-\uFF3A\uFF41-\uFF5A\uFF66-\uFFBE\uFFC2-\uFFC7\uFFCA-\uFFCF\uFFD2-\uFFD7\uFFDA-\uFFDC-_0-9]+$/


// Adds the skill to the set and resets the input.
function addSkill() {
    let newSkill = document.getElementById("add_skill_input").value
    if (newSkill.slice(-1) === " ") {
        newSkill = newSkill.slice(0, -1);
    }

    if (skills.has(newSkill)) {
        document.getElementById("add_skill_input").value = "";
        return;
    }

    if (newSkill.slice(-1) === " ") {
        newSkill = newSkill.slice(0, -1);
    }

    // check to see if it matches the correct skill format
    if(validateSkill(newSkill)) {
        document.getElementById("skill_error").style = "color:red;";
        return
    }

    document.getElementById("skill_error").style = "display:none;";

    skills.add(newSkill)
    if (allSkills !== null) {
        allSkills = allSkills.filter(s => s !== newSkill)
        autocomplete(document.getElementById("add_skill_input"), allSkills, "skill")
    }
    document.getElementById("add_skill_input").value = ""
    appendSkill(newSkill)
}

function validateSkill(newSkill) {
    const validate = newSkill.match(skillPattern)
    return (!validate || !validate.length == 1 || newSkill.length > skillCharLimit || newSkill.toLowerCase().includes("no_skill") || /^([_-])+$/.test(newSkill));
}

function updateSkills() {
    const skillCounter = document.getElementById("skillCharCount")
    const skillInput = document.getElementById("add_skill_input")
    skillCounter.innerText = "Characters Remaining: "  + (50 - skillInput.value.length) + ", press DELETE to remove skills"
}

// Inserts a new skill tag into the form
function appendSkill(skillText) {
    let skillTag = document.createElement("button");
    skillTag.id = "skill_" + skillText
    skillTag.className = "table_text skill_tag"
    skillTag.innerText = skillText + " ✖"
    skillTag.value = skillText
    skillTag.onclick = removeSkill;
    skillRow.appendChild(skillTag);
    if (skillRow.getBoundingClientRect().right < skillTag.getBoundingClientRect().right) {
        skillRow.removeChild(skillTag)
        insertSkillRow()
        skillRow.appendChild(skillTag);
    }
    storeSkills()
}

/* Creates a new row for skill tags to be placed on, for some reason inserting <br> couldn't do this*/
function insertSkillRow() {
    let newSkillRow = document.createElement("div");
    newSkillRow.style.display = "flex"
    document.getElementById("skills_container").appendChild(newSkillRow)
    skillRow = newSkillRow
}

// Deletes a skill then rebuilds the skill list
function removeSkill(event) {
    skills.delete(event.target.value)
    allSkills.push(event.target.value)
    autocomplete(document.getElementById("add_skill_input"), allSkills, "skill")
    clearSkills()
    insertSkillRow()
    for (const skill of skills) {
        appendSkill(skill)
    }
    return false;
}

// Removes all skills from display
function clearSkills() {
    let skillContainer = document.getElementById("skills_container")
    while (skillContainer.lastChild) {
        skillContainer.removeChild(skillContainer.lastChild)
    }
}


// Event listener to add links when enter is pressed and the link input is selected
setTimeout(() => {document.querySelector("#add_link_input").addEventListener("keyup", event => {
                      if (event.key !== "Enter") return;
                      addLink()
                  })}, 20)


links = new Set();
linksContainer = document.getElementById("links_container")
setTimeout(() => {linksContainer = document.getElementById("links_container")}, 10)
linkCounter = 0;

// Adds the link to the set and resets the input.
function addLink() {
    let newLink = document.getElementById("add_link_input").value
    if (newLink === "") {
        return;
    }
    if (!validateLink(newLink)) {
        document.getElementById("link_error").style = "color:red;";
        return;
    }
    document.getElementById("link_error").style = "display:none;";
    if (links.has(newLink)) return;
    links.add(newLink)
    document.getElementById("add_link_input").value = ""
    appendLink(newLink)
}

// Validate a link
function validateLink(link) {
    let pattern = /https?:\/\/[-a-zA-Z0-9@:%_\+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_\+.~#?&//=]*)?/gi;
    return pattern.test(link);
}

// Inserts a new link into the form
function appendLink(linkText) {
    let linkBox = document.createElement("div");
    linksContainer.appendChild(linkBox)
    linkBox.className = "trim_text"
    linkBox.style.display = "flex"
    let newLink = document.createElement("a");
    newLink.id = "link_" + linkText
    newLink.className = "sprint-description display_data text_button trim_text"
    newLink.style.fontSize = "110%"
    newLink.width = "90% !important"
    newLink.style.display = "inline-block"
    newLink.target = "_blank"
    linkCounter++
    newLink.innerText = linkCounter + ".  " + linkText
    newLink.href = linkText
    let linkDelete = document.createElement("button");
    linkDelete.className = "fa fa-trash each_link_delete"
    linkDelete.value = linkText
    linkDelete.onclick = removeLink;
    linkBox.appendChild(newLink)
    linkBox.appendChild(linkDelete)
    storeLinks()
}

// Store the javascript set of links into a hidden HTML divider,
// which can be retrieved in Java from the linkInput ID
// This is to transfer the frontend set of links into the backend.
function storeLinks() {
    let linksInput = document.getElementById("linksInput")
    const linkList = Array.from(links).join(' ');
    linksInput.value = linkList
}

// Deletes a link then rebuilds the link list
function removeLink(event) {
    links.delete(event.target.value)
    clearLinks()
    linkCounter = 0
    for (const link of links) {
        appendLink(link)
    }
    storeLinks()
    return false;
}

// Removes all links from display
function clearLinks() {
    let linkContainer = document.getElementById("links_container")
    while (linkContainer.lastChild) {
        linkContainer.removeChild(linkContainer.lastChild)
    }
}

function updateTitle() {
    const titleCounter = document.getElementById("titleCharCount")
    const titleInput = document.getElementById("evidence_title")
    titleCounter.innerText = "Characters Remaining: "  + (100 - titleInput.value.length)
}

function updateDesc() {
    const descCounter = document.getElementById("descCharCount")
    const descInput = document.getElementById("evidence_desc")
    descCounter.innerText = "Characters Remaining: "  + (2000 - descInput.value.length)
}

function verifyTitle() {
    const titleData = document.getElementById("evidence_title").value.replace(/[\n\r ]/g, '');
    if (titleData.match(allChrPattern) == null || titleData.match(emojiPattern) != null) {
        if (titleData !== "") {
            document.getElementById("title_error").style = "color:red;";
        }
        return false
    } else {
        document.getElementById("title_error").style = "display:none;";
        return true
    }
}

function verifyDesc() {
    const descData = document.getElementById("evidence_desc").value.replace(/[\n\r ]/g, '');
    if (descData.match(allChrPattern) == null || descData.match(emojiPattern) != null) {
        if (descData !== "") {
            document.getElementById("description_error").style = "color:red;";
        }
        return false
    } else {
        document.getElementById("description_error").style = "display:none;";
        return true
    }
}

function canSaveCheck() {
    const saveButton = document.getElementById("projectSave")
    //https://stackoverflow.com/questions/15861088/regex-to-match-only-language-chars-all-language
    if (!verifyDesc() || !verifyTitle()) {
        saveButton.style = "background-color: #AAAAAA;"
        saveButton.setAttribute("disabled","disabled");
    } else {
        saveButton.style = "background-color: #26D20F;"
        saveButton.removeAttribute("disabled");
    }
}

function dateCheck() {
    const dateInput = document.getElementById("date_input")
    if (Date.parse(dateInput.value) < Date.parse(dateInput.min) || Date.parse(dateInput.value) > Date.parse(dateInput.max)) {
        document.getElementById("date_error").style = "color:red;";
    } else {
        document.getElementById("date_error").style = "display:none;";
    }

}

function updateUsers() {
    const userCounter = document.getElementById("userCharCount")
    const userInput = document.getElementById("add_user_input")
    userCounter.innerText = "Characters Remaining: "  + (65 - userInput.value.length)

}

// Enable form and hide add evidence form
function displayAddEvidenceForm() {
    document.getElementById('evidence_form').style = "display: block";
    document.getElementById('add_button').style = "display: none";
}

// Enable add evidence button and hide form
function displayAddButton() {
    document.getElementById('evidence_form').style = "display: none";
    document.getElementById('add_button').style = "display: block";
}


