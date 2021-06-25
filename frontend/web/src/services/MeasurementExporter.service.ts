import {MeasurementModel} from "@/models/measurement.model";
import DateService from "./Date.service";

class MeasurementExporterService {

    exportToCSV(measurements: MeasurementModel[]) {
        const csvSeparator = ",";
        const eol = "\r\n";

        let output = `time${csvSeparator}temperature${csvSeparator}humidity${eol}`;

        for (let i = 0; i < measurements.length; i++) {
            const measurement = measurements[i];
            const dateString = DateService.formatDate(measurement.time, true);
            const line = `"${dateString}"${csvSeparator}${measurement.temperature}${csvSeparator}${measurement.humidity}${eol}`;
            output += line;
        }

        MeasurementExporterService.downloadFile("measurements.csv", "text/csv", output);
    }

    exportToJSON(measurements: MeasurementModel[]) {
        const processedMeasurements = [];
        for (let i = 0; i < measurements.length; i++) {
            const measurement = measurements[i];
            const dateString = DateService.formatDate(measurement.time, true);
            processedMeasurements.push({
               time: dateString,
               temperature: measurement.temperature,
               humidity: measurement.humidity
            });
        }

        const content = JSON.stringify(processedMeasurements);
        MeasurementExporterService.downloadFile("measurements.json", "application/json", content);
    }

    exportToXML(measurements: MeasurementModel[]) {
        const xml = document.implementation.createDocument(null, "measurements");

        for (let i = 0; i < measurements.length; i++) {
            const measurement = measurements[i];
            const measurementNode = xml.createElement("measurement");

            const timeNode = xml.createElement("time");
            timeNode.textContent = DateService.formatDate(measurement.time, true);
            measurementNode.appendChild(timeNode);

            const tempNode = xml.createElement("temperature");
            tempNode.textContent = measurement.temperature.toString();
            measurementNode.appendChild(tempNode);

            const humNode = xml.createElement("humidity");
            humNode.textContent = measurement.humidity.toString();
            measurementNode.appendChild(humNode);

            xml.documentElement.appendChild(measurementNode);
        }

        const serializer = new XMLSerializer();
        const serialized = serializer.serializeToString(xml);
        MeasurementExporterService.downloadFile("measurements.xml", "text/xml", serialized);
    }

    private static downloadFile(fileName: string, contentType: string, content: string) {
        const blob = new Blob([content], {type: contentType});
        const url = window.URL.createObjectURL(blob);

        const a = document.createElement("a") as HTMLAnchorElement;
        a.style.display = "none";
        a.download = fileName;
        a.href = url;
        document.body.appendChild(a);

        a.click();
        window.URL.revokeObjectURL(url);
        a.remove();
    }
}

export default new MeasurementExporterService();