# VINScanner

VINScanner is an Android app designed to scan Vehicle Identification Numbers (VINs) and retrieve associated vehicle information, recall history, and complaint details. It utilizes the NHTSA Vehicle API, Recall API, and Complaint API, along with a local MySQL database to store previous queries and enhance user experience.

## Features

- **Scan VINs**: Effortlessly scan VINs using the device's camera.
- **Manual Entry**: Users have the option to input VINs manually.
- **Vehicle Information**: Detailed insights about the scanned or manually entered VINs, including make, model, year, and other pertinent details.
- **Recall History**: Obtain the recall history for specific vehicles, ensuring safety awareness.
- **NHTSA Vehicle API Integration**: Seamless integration with the NHTSA Vehicle API for comprehensive vehicle data and recall insights.

## Installation

To set up the VINScanner app, adhere to the steps below:

1. Download the APK file from the [releases](https://github.com/Ismael-Deka/VINScanner/releases) section.
2. Transfer the APK file to your Android device.
3. Navigate on your Android device to **Settings > Security**.
4. Activate the **"Unknown sources"** option, allowing installations outside the Play Store.
5. Locate the APK file on your device and initiate the installation.
6. Adhere to the prompts to finalize the installation.


## Usage

1. Initiate the VINScanner app on your Android device.
2. If prompted, grant camera access permissions.
3. For VIN scanning, align your device's camera to view the VIN within the frame. The app will identify and process the VIN automatically.
4. Alternatively, opt for manual entry via the on-screen keyboard.
5. Upon VIN scanning or entry, the app will fetch and exhibit the vehicle details and recall history.
6. Navigate through the displayed vehicle data and recall specifics as required.

## Dependencies

The VINScanner app is dependent on:

- **NHTSA Vehicle API**: For comprehensive vehicle details.
- **NHTSA Recall API**: For recall data retrieval.
- **NHTSA Complaint API**: For vehicle complaint details.
- **MySQL Database**: A local database for storing and retrieving previous VIN queries. Ensure appropriate database setup and configuration in the app.

## License

This project is licensed under the [GNU GENERAL PUBLIC LICENSE](LICENSE).

## Contact

For inquiries, suggestions, or feedback related to VINScanner, you can reach out to me at [ismael.deka@gmail.com](mailto:ismael.deka@gmail.com).
