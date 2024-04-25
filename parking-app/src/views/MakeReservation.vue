<template>
  <div class="max-w-7xl mx-auto pb-12 px-4 sm:px-6 lg:px-8">
    <div class="bg-white overflow-hidden shadow rounded-lg">
      <div class="px-4 py-5 sm:p-6">
        <div class="md:grid md:grid-cols-3 md:gap-6">
          <div class="md:col-span-1">
            <h3 class="text-lg font-medium leading-6 text-gray-900">Make a reservation</h3>
            <p class="mt-1 text-sm text-gray-500">
              Enter your license plate and select one of available spots to reserve them.
            </p>
          </div>
          <div class="mt-5 md:mt-0 md:col-span-2">
            <div class="space-y-6">
            <div>
              <label for="license-plate" class="block text-sm font-medium text-gray-700">License Plate *</label>
              <div class="mt-1">
                <input v-model="newReservation.licensePlate" type="text" id="license-plate" class="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border-gray-300 rounded-md" placeholder="AA-00-BB">
              </div>
            </div>

              <!--            <div class="grid grid-cols-3 gap-6">-->
  <!--              <div class="col-span-3 sm:col-span-2">-->
  <!--                <label for="duration" class="block text-sm font-medium text-gray-700">-->
  <!--                  Duration-->
  <!--                </label>-->
  <!--                <div class="mt-1 flex rounded-md shadow-sm">-->
  <!--                  <input v-model="newReservation.duration" type="number" id="duration" class="focus:ring-blue-500 focus:border-blue-500 flex-1 block w-full rounded-none rounded-l-md sm:text-sm border-gray-300">-->
  <!--                  <span class="inline-flex items-center px-3 rounded-r-md border border-l-0 border-gray-300 bg-gray-50 text-gray-500 text-sm">-->
  <!--                    minutes-->
  <!--                  </span>-->
  <!--                </div>-->
  <!--              </div>-->
  <!--            </div>-->
  <!--            <div class="grid grid-cols-2">-->
  <!--              <div class="col-span-1">-->
  <!--                <label for="date" class="block text-sm font-medium text-gray-700">-->
  <!--                  Date-->
  <!--                </label>-->
  <!--                <div class="mt-1">-->
  <!--                  <date-picker id="date" inline v-model="newReservation.date" type="date"></date-picker>-->
  <!--                </div>-->
  <!--              </div>-->
  <!--              <div class="col-span-1">-->
  <!--                <label for="time" class="block text-sm font-medium text-gray-700">-->
  <!--                  Time-->
  <!--                </label>-->
  <!--                <div class="mt-1">-->
  <!--                  <time-picker id="time" inline v-model="newReservation.time" type="time" :show-second="false"></time-picker>-->
  <!--                </div>-->
  <!--              </div>-->
  <!--            </div>-->
              <div>
                <label class="block text-sm font-medium text-gray-700">
                  Which spot do you want to reserve?
                </label>
                <fieldset class="mt-2">
                  <div class="bg-white rounded-md -space-y-px">
                    <div class="relative border rounded-tl-md rounded-tr-md p-4 flex" v-for="spot in availableSpots" :key="spot.id">
                      <div class="flex items-center h-5">
                        <input :id="'spot-option-'+spot.id" v-model="newReservation.spotId" :value="spot.id" name="product-option" type="radio" class="focus:ring-blue-500 h-4 w-4 text-blue-600 cursor-pointer border-gray-300">
                      </div>
                      <label :for="'spot-option-'+spot.id" class="ml-3 flex flex-col cursor-pointer">
                        <span class="block text-sm font-medium">
                          Spot {{ spot.id }} at {{spot.lotName}}
                        </span>
                        <span class="block text-sm">
                          Rate {{spot.rate | perHour}} per hour
                        </span>
                      </label>
                    </div>
                  </div>
                </fieldset>
              </div>
            </div>
          </div>
          <div class="md:col-span-1 mt-6">
            <h3 class="text-lg font-medium leading-6 text-gray-900">Overview of reservation</h3>
            <p class="mt-1 text-sm text-gray-500">
              Check your information before making the reservation.
            </p>
          </div>
          <div class="mt-6 md:col-span-2">
            <div class="border-t border-gray-200 px-4 py-5 sm:p-0">
              <dl class="sm:divide-y sm:divide-gray-200">
                <div class="py-4 sm:py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt class="text-sm font-medium text-gray-500">
                    License Plate
                  </dt>
                  <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                    {{ newReservation.licensePlate||"Not yet filled in"}}
                  </dd>
                </div>
                <div class="py-4 sm:py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                  <dt class="text-sm font-medium text-gray-500">
                    Spot
                  </dt>
                  <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                    {{ newReservation.spotId || "Not yet selected" }}
                  </dd>
                </div>
              </dl>
            </div>
          </div>
        </div>
        <div class="pt-5 border-t">
          <div class="flex justify-end">
            <button @click="makeReservation()" class="ml-3 inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
              Make reservation
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>


</template>

<script>
import {mapActions, mapGetters, mapState} from "vuex";
import * as moment from "moment";
// import DatePicker from 'vue2-datepicker';
// import TimePicker from 'vue2-datepicker';
export default {
  name: "MakeReservation",
  components: {
    // DatePicker,
    // TimePicker
  },
  data() {
    return {
      newReservation: {
        licensePlate: null,
        spotId: null,
      },
      moment: moment
    }
  },
  computed: {
    ...mapState({
      loadingSpots: state => state.parking.loading.spots,
      user: state => state.auth.user
    }),
    ...mapGetters('parking', [
        "availableSpots"
      ])
  },
  methods: {
    ...mapActions('parking', [
        '_getParkingSpots',
        '_makeReservation'
    ]),
    getLots(){
      this._getParkingSpots();
    },
    makeReservation(){
      this._makeReservation(this.newReservation).then(() => {
        if(this.user.role === "ADMIN") {
          this.$router.push({name: "admin-home"})
        }
        else {
          this.$router.push({name: "home"})
        }

      });
    }
  },
  mounted() {
    this.getLots();
  },
  filters: {
    perHour(value){
      const formatter = new Intl.NumberFormat('nl-NL', {
        style: 'currency',
        currency: 'EUR',
        minimumFractionDigits: 2
      })
      return formatter.format(value*60/100);

    }
  }
}
</script>

<style lang="scss">
  //datepicker
  $primary-color: #3B82F6;
  @import '~vue2-datepicker/scss/index.scss';
</style>
