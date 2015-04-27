void quickSort(int arr[], int left, int right){
        int index;
		index = partition(arr, left, right);
        if(index < right)
            quickSort(arr, index, right);
        if(left < index - 1)
            quickSort(arr, left, index - 1);
    }
    
int partition(int arr[], int left, int right){
        int pivot;
        int lIndex;
        int rIndex;
		pivot = arr[(right + left) / 2];
		lIndex = left - 1;
        rIndex = right + 1;

        while (1) {
            while (arr[++lIndex] < pivot);
            while (arr[--rIndex] > pivot);

            if (lIndex > rIndex) return;

            int temp;
			temp = arr[lIndex];
            arr[lIndex] = arr[rIndex];
            arr[rIndex] = temp;
        }
    return lIndex;
    }
	in%tf=14 413 xc4;