<template>
  <div class="max-w-7xl mx-auto pb-12 px-4 sm:px-6 lg:px-8">
    <div class="bg-white shadow overflow-hidden sm:rounded-md">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
        <tr>
          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            Spot Id
          </th>
          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            License plate
          </th>
          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            Arrival time
          </th>
          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            Duration
          </th>
          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            Cost
          </th>
        </tr>
        </thead>
        <tbody>
        <!-- Odd row -->
        <tr class="bg-white" v-for="entry in entries" :key="entry.id">
          <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
            {{entry.spotId}}
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            <span class="flex-shrink-0 inline-block px-2 py-0.5 text-green-800 text-xs font-medium bg-green-100 rounded-full">{{entry.licensePlate}}</span>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            {{ moment(entry.enterTime).fromNow() }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500" v-if="entry.leaveTime !== null">
            {{ moment.utc(moment(entry.leaveTime).diff(entry.enterTime)).format("HH:mm") }}
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500" v-else>
            Still in parking lot
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            {{entry.cost | price}}
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>


</template>

<script>
import {mapActions, mapState} from "vuex";
import * as moment from "moment";
export default {
  name: "Entries",
  components: {},
  data() {
    return {
      moment: moment
    }
  },
  computed: {
    ...mapState({
        loadingEntries: state => state.parking.loading.entries,
        entries: state => state.parking.entries
    })
  },
  methods: {
    ...mapActions('parking', [
        '_getEntries',
    ]),
  },
  mounted() {
    this._getEntries();
  },
  filters: {
    price(value){
      if(value == null){
        value = 0;
      }
      const formatter = new Intl.NumberFormat('nl-NL', {
        style: 'currency',
        currency: 'EUR',
        minimumFractionDigits: 2
      })
      return formatter.format(value);

    }
  }
}
</script>
