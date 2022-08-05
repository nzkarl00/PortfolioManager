function getSprintTimeBound(timeBoundList, start, end) {
    let startDate = Date.parse(start)
    let endDate = Date.parse(end)
    let returning = [];
    for (const timeBoundItem of timeBoundList) {
        let date = Date.parse(timeBoundItem.startDate)
        if (date >= startDate && date <= endDate) {
            returning.push(timeBoundItem)
        }
    }
    return returning
}

function getCalendarTimeBound(timeBoundList, start) {
    let startDate = Date.parse(start)
    let endDate = Date.parse(start)+86400000
    let returning = [];
    for (const timeBoundItem of timeBoundList) {
        let date = Date.parse(timeBoundItem.startDate)
        if (date >= startDate && date <= endDate) {
            returning.push(timeBoundItem)
        }
    }
    return returning
}