class DateService {
    formatDate(timestamp: number) {
        // 2021-02-11 12:53
        const date = new Date(timestamp * 1000);

        const month = (date.getMonth() + 1).toString().padStart(2, "0");
        const day = date.getDate().toString().padStart(2, "0");
        const hour = date.getHours().toString().padStart(2, "0");
        const minute = date.getMinutes().toString().padStart(2, "0");

        return `${date.getFullYear()}-${month}-${day} ${hour}:${minute}`;
    }
}

export default new DateService();