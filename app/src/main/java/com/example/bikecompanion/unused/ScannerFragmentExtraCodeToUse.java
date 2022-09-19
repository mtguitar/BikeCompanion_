package com.example.bikecompanion.unused;

import androidx.fragment.app.Fragment;

import com.example.bikecompanion.adapters.scanner.RecyclerViewInterface;

public class ScannerFragmentExtraCodeToUse extends Fragment implements RecyclerViewInterface {
    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onButtonClick(int position) {

    }
/*
    private static final String TAG = "FlareLog ScanFrag";

    //scanning vars
    private boolean scanning = true;
    private String serviceUuids;
    private String deviceType;
    private ArrayList scanResults;
    private RecyclerView recyclerView;
    private String deviceName;

    //view vars
    private View view;
    private static Button buttonStopScan;
    private static TextView textViewScanTitle;
    private static View progressBarScan;
    private static TextView textViewDeviceManufacturer;
    private static TextView textViewDeviceType;
    private static TextView textViewDeviceBattery;
    private static View constraintLayoutScanResult;
    private static Button buttonAddToMyDevices;

    //connection vars
    private String deviceMacAddress;
    private Intent gattServiceIntent;
    private BleConnectionService bleConnectionService;
    private boolean boundToService = false;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //vars passed from other fragment
        deviceName = ScannerFragmentArgs.fromBundle(getArguments()).getName();
        deviceMacAddress = ScannerFragmentArgs.fromBundle(getArguments()).getMacAddress();
        serviceUuids = ScannerFragmentArgs.fromBundle(getArguments()).getServiceUuids();
        deviceType = ScannerFragmentArgs.fromBundle(getArguments()).getDeviceType();

        //SendCommand to BleScannerService to start scanning
        sendCommandToService(BleScannerService.class, Constants.ACTION_START_OR_RESUME_SERVICE);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_scanner, container, false);
        setupViews();
        observeLiveData();
        registerBroadcastReceiver();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(scanResults != null) {
            scanResults.clear();
            updateRecycleViewer(scanResults);
        }
    }

    private void setupViews(){
        buttonStopScan = (Button)view.findViewById(R.id.buttonStopScan);
        textViewScanTitle = view.findViewById(R.id.textViewScanTitle);
        progressBarScan = view.findViewById(R.id.progressBarScan);
        recyclerView = view.findViewById(R.id.recyclerViewScanner);
        buttonAddToMyDevices = view.findViewById(R.id.button_scanner_add_to_my_devices);

        //creates recyclerView but does not show until there is data in it
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        if (scanResults != null){
            scanResults.clear();
            updateRecycleViewer(scanResults);
        }

        //set scan/stopScan button onclick listener
        buttonStopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(scanning){
                    sendCommandToService(BleScannerService.class, Constants.ACTION_STOP_SERVICE);
                }
                else{
                    if(scanResults != null) {
                        scanResults.clear();
                        updateRecycleViewer(scanResults);
                    }
                    sendCommandToService(BleScannerService.class, Constants.ACTION_START_OR_RESUME_SERVICE);
                }
            }
        });

    }

    //connects to device and reads characteristics when user clicks on a scan result
    @Override
    public void onItemClick(int position) {

        //Get device info when clicked
        ScannerAdapter scannerAdapter = new ScannerAdapter(scanResults, this);
        deviceName = scannerAdapter.scanResultsArrayList.get(position).getDeviceName();
        deviceMacAddress = scannerAdapter.scanResultsArrayList.get(position).getDeviceMacAddress();
        deviceType = scannerAdapter.scanResultsArrayList.get(position).getDeviceType();

        constraintLayoutScanResult = getView().findViewById(R.id.constraint_layout_scan_result);
        int visibility = constraintLayoutScanResult.getVisibility();

        if (visibility == View.GONE) {
            //connectDevice(deviceMacAddress);
            sendCommandToService(BleScannerService.class, Constants.ACTION_STOP_SERVICE);
        }
        else{
            //bleConnectionService.disconnectDevice(deviceMacAddress);
            Log.d(TAG, "Trying to disconnect device");

        }

    }

    @Override
    public void onButtonClick(int position) {
        ScannerAdapter scannerAdapter = new ScannerAdapter(scanResults, this);
        String deviceName = scannerAdapter.scanResultsArrayList.get(position).getDeviceName();
        String deviceMacAddress = scannerAdapter.scanResultsArrayList.get(position).getDeviceMacAddress();
        String deviceType = scannerAdapter.scanResultsArrayList.get(position).getDeviceType();
        Device newDevice = new Device(deviceName, deviceName, deviceMacAddress, deviceType);

        DeviceRepository deviceRepository = new DeviceRepository((Application) getActivity().getApplicationContext());
        deviceRepository.insert(newDevice);

        NavDirections action = ScannerFragmentDirections.actionNavScannerToNavMyDevices();
        Navigation.findNavController(getView()).navigate(action);

    }


    private void observeLiveData(){
        //Setup observer of livedata for recyclerView, calls updateRecyclerView when data changes
        final Observer<ArrayList<ScannerListenerInterface>> observerScanResults;
        observerScanResults = new Observer<ArrayList<ScannerListenerInterface>>(){
            public void onChanged(@Nullable final ArrayList scanResults) {
                updateRecycleViewer(scanResults);
            }
        };

        //start observing scan results
        BleScannerService.getScanResults().observe(getActivity(), observerScanResults);
    }


    public void updateRecycleViewer(ArrayList scanResults){
        ScannerAdapter scannerAdapter = new ScannerAdapter(scanResults, this);
        recyclerView.setAdapter(scannerAdapter);
        this.scanResults = scanResults;
    }





    //Sends intents to services (only used for scanner service right now)
    private void sendCommandToService(Class serviceClass, String action) {
        Intent bleServiceIntent = new Intent(requireContext(), serviceClass);
        bleServiceIntent.setAction(action);
        if(serviceUuids != null) {
            bleServiceIntent.putExtra("serviceUuids", serviceUuids);
        }
        if(deviceType != null) {
            bleServiceIntent.putExtra("deviceType", deviceType);
        }
        requireContext().startService(bleServiceIntent);
        Log.d(TAG, "Sent intent to " + serviceClass + " " + action);
    }


    private void registerBroadcastReceiver(){
        //Set intent filters and register receiver to listen for updates
        scannerUpdateIntentFilter();
        getActivity().registerReceiver(scannerUpdateReceiver, scannerUpdateIntentFilter());
    }

    //Intent filters for receiving intents
    private static IntentFilter scannerUpdateIntentFilter () {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_BLE_SCANNING_STARTED);
        intentFilter.addAction(Constants.ACTION_BLE_SCANNING_STOPPED);
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    //Broadcast receiver that changes buttons and textview upon receiving intents from service
    private BroadcastReceiver scannerUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "Received broadcast with action " + action);
            if (Constants.ACTION_BLE_SCANNING_STARTED.equals(action)){
                scanning = true;
                textViewScanTitle.setText("Scanning for devices");
                buttonStopScan.setText("Stop Scan");
                progressBarScan.setVisibility(View.VISIBLE);
            }
            if (Constants.ACTION_BLE_SCANNING_STOPPED.equals(action)){
                scanning = false;
                if (scanResults == null){
                    textViewScanTitle.setText("No devices found");
                }
                else {
                    textViewScanTitle.setText("Select to add to myDevices");
                }
                buttonStopScan.setText("Scan Again");
                buttonStopScan.setEnabled(true);
                progressBarScan.setVisibility(View.GONE);
            }
            if (ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                bleConnectionService.readCharacteristic(GenericDevice.UUID_SERVICE_BATTERY, GenericDevice.UUID_CHARACTERISTIC_BATTERY, GenericDevice.DATA_TYPE_BATTERY);
                Log.d(TAG, "Sent intent to " + BleScannerService.class + " " + Constants.ACTION_READ_CHARACTERISTIC);

            }
            if (ACTION_DATA_AVAILABLE.equals(action)){
                String characteristic = intent.getStringExtra(Constants.EXTRA_DATA);
                Log.d(TAG, "received characteristic " + characteristic);
                //textViewDeviceBattery.setText(characteristic);


            }


        }
    };

    // Checks if BluetoothLeService is bound
    // If not bound, binds to BluetoothLeService and calls serviceConnection
    // If bound, call BluetoothLeService's connected method directly, passing deviceMacAddress
    public void connectDevice(String deviceMacAddress){
        if (!boundToService) {
            Log.d(TAG, "Not bound, trying to connect " + deviceMacAddress);
            gattServiceIntent = new Intent(getActivity(), BleConnectionService.class);
            getActivity().bindService(gattServiceIntent, serviceConnection, getActivity().BIND_AUTO_CREATE);
        }
        else {
            bleConnectionService.connectDevice(deviceMacAddress);
            Log.d(TAG, "Already bound, trying to connect " + deviceMacAddress);
        }
    }


    // Code to manage BleConnectionService lifecycle.
    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            bleConnectionService = ((BleConnectionService.LocalBinder) service).getService();
            if (!bleConnectionService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.
            boundToService = true;
            bleConnectionService.connectDevice(deviceMacAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            boundToService = false;
            bleConnectionService = null;
        }
    };
*/
}