import React, {useEffect, useState} from 'react'
import {useRecoilState, useRecoilValue} from 'recoil';
import {Range, Result} from 'antd-mobile';

import {
    buildingState,
    filterAreaState,
    regionState,
    tradeTypeState,
    versusRegionListState
} from '../../datatool/state';
import {getChart, getDetail, getRegionChart, getVersusChart} from '../../datatool/api';

import classNames from 'classnames/bind';
import styles from '../detail/versusChart.module.scss';
import {Line} from 'react-chartjs-2';
import Chart from 'chart.js';
import moment from 'moment';
import {CHART_COLOR_LIST, getEndArea, getStartArea} from '../../constant';
import Loading from './Loading';

const cx = classNames.bind(styles);

const VersusChart = () => {
    const [chartList, setChartList] = useState(null);
    const [activeChartIndex, setActiveChartIndex] = useState(null);
    const [filterArea, setFilterArea] = useRecoilState(filterAreaState);
    const region = useRecoilValue(regionState);
    const [tradeType, setTradeType] = useRecoilState(tradeTypeState);
    const [building, setBuilding] = useRecoilState(buildingState);

    const [period, setPeriod] = useState([moment('20160101', 'YYYYMMDD'), moment()]);
    const [startDate, setStartDate] = useState(period[0]);
    const [endDate, setEndDate] = useState(period[1]);
    const [isLoading, setIsLoading] = useState(false);
    const [versusRegionList, setVersusRegionList] = useRecoilState(versusRegionListState);

    useEffect(() => {
        // fetchChart();
        // initChartEvent();
        const init = async () => {
            if (region) {
                if (region.type === 'BUILDING') {
                    let building = await getDetail(region.buildingId);
                    setBuilding(building);
                    if (versusRegionList.length === 0) {
                        setVersusRegionList([building]);
                    }
                } else {
                    if (versusRegionList.length === 0) {
                        setVersusRegionList([region]);
                    }
                }
            }
        };
        init();
        initChartEvent();
    }, []);

    useEffect(() => {
        fetchChart();
    }, [region, startDate, endDate, filterArea, tradeType, versusRegionList]);

    // useEffect(() => {
    //     if (chartList) {
    //         for (const chart of [...chartList.datasets]) {
    //             if (chart.label === tradeType) {
    //                 chart.borderColor = '#00802E';
    //             } else {
    //                 chart.borderColor = '#DDDDDD';
    //             }
    //         }
    //
    //         setChartList({
    //             labels: [...chartList.labels],
    //             datasets: [...chartList.datasets],
    //         });
    //     }
    // }, [tradeType]);

    // useEffect(() => {
    //     if (chartList && chartList.datasets[0].data.length > 0 && activeChartIndex && activeChartIndex !== -1) {
    //         // setTradeDate(moment(chartList.datasets[0].data[activeChartIndex].x));
    //         setCount(chartList.datasets[0].data[activeChartIndex].z);
    //     }
    // }, [activeChartIndex]);

    const chartPlugin = (chart, callback) => {
        if (chart.config.type !== 'line') {
            return;
        }
        if (chart.tooltip._active && chart.tooltip._active.length) {
            if (chart.tooltip._active.filter(x => x._datasetIndex === 0).length > 0) {
                const activePoint = chart.tooltip._active.filter(x => x._datasetIndex === 0)[0];
                const ctx = chart.ctx;
                const x = activePoint.tooltipPosition().x;
                const topY = chart.legend.bottom;
                const bottomY = chart.chartArea.bottom;
                ctx.save();
                ctx.beginPath();
                ctx.moveTo(x, topY);
                ctx.lineTo(x, bottomY);
                ctx.lineWidth = 2;
                ctx.pointRadius = 2;
                ctx.strokeStyle = '#313131';
                ctx.stroke();
                ctx.restore();

                handleChartClick(activePoint._index);
            }
        }
    };

    const customTooltip = (elements, position) => {
        if (!elements.length) {
            return false;
        }

        let offset = 0;
        //adjust the offset left or right depending on the event position
        if (elements[0]._chart.width / 2 > position.x) {
            offset = 20;
        } else {
            offset = -20;
        }
        return {
            x: position.x + offset,
            y: 0
        }
    };

    const initChartEvent = () => {
        Chart.pluginService.register({afterDraw: chartPlugin});
        Chart.Tooltip.positioners.custom = customTooltip;
    };

    const handleChartClick = (index) => {
        setActiveChartIndex(index);
    };

    const groupBy = (items, key) => items.reduce(
        (result, item) => ({
            ...result,
            [item[key]]: [
                ...(result[item[key]] || []),
                item,
            ],
        }),
        {},
    );

    const getTradeChart = async (regionParams, label, color) => {
        let result = [];
        let data = [];
        let startArea = getStartArea(filterArea[0]);
        let endArea = getEndArea(filterArea[1]);

        if (regionParams.type === 'BUILDING') {
            result = await getVersusChart(tradeType, regionParams.buildingId, startArea, endArea, startDate.format('YYYYMM') + '01', endDate.format('YYYYMM') + '31');
        } else {
            result = await getRegionChart(tradeType, regionParams.id, startArea, endArea, startDate.format('YYYYMM') + '01', endDate.format('YYYYMM') + '31');
        }

        if (result.length == 0) {
            return {
                labels: [],
                datasets: []
            };
        }

        let groupList = groupBy(result.map(x => {
            x.yearMonth = x.date.substring(0, 6);
            return x;
        }), 'yearMonth');

        let sDate = moment(startDate.format('YYYYMM') + '01', 'YYYYMMDD');
        let eDate = moment(endDate.format('YYYYMM') + '01', 'YYYYMMDD');

        while (!sDate.isAfter(eDate)) {
            let beforeKey = moment(sDate).subtract(1, 'months').format('YYYYMM');
            let key = sDate.format('YYYYMM');
            sDate = sDate.add(1, 'months');
            let total = groupList[key];
            let beforeTotal = groupList[beforeKey];
            if (!total) {
                if (!beforeTotal) {
                    beforeTotal = groupList[Object.keys(groupList)[0]];
                }
                groupList[key] = beforeTotal.map(x => {
                    return {date: key, price: x.price, area: x.area, yearMonth: key}
                });
                groupList[key].isEmpty = true;
            }
        }

        groupList = Object.keys(groupList).map(key => {
            return ({
                x: key,
                y: (groupList[key].map(x => x.price * 1).reduce((a, b) => a + b) / groupList[key].length).toFixed(0),
                z: groupList[key].length
            })
        });

        let total = {
            pointRadius: 0,
            borderWidth: 2,
            // spanGaps: true,
            borderColor: color,
            backgroundColor: color,
            fill: false,
            label: label,
            data: groupList
        };

        data = [total];

        result = result.map(x => moment(x.date)).sort((a, b) => a - b);
        // result.push(moment().add(5, 'months'));

        return {labels: result, datasets: data}
    };

    const getRentChart = async () => {
        let result = [];
        let data = [];
        if (region.type === 'BUILDING') {
            result = await getChart('rent', region.buildingId, '', startDate.format('YYYYMM') + '01', endDate.format('YYYYMM') + '31');
        } else {
            let startArea = getStartArea(filterArea[0]);
            let endArea = getEndArea(filterArea[1]);
            result = await getRegionChart('rent', region.id, startArea, endArea, startDate.format('YYYYMM') + '01', endDate.format('YYYYMM') + '31');
        }

        let groupList = groupBy(result.map(x => {
            x.yearMonth = x.date.substring(0, 6);
            return x;
        }), 'yearMonth');

        let sDate = moment(startDate.format('YYYYMM') + '01', 'YYYYMMDD');
        let eDate = moment(endDate.format('YYYYMM') + '01', 'YYYYMMDD');

        while (!sDate.isAfter(eDate)) {
            let beforeKey = moment(sDate).subtract(1, 'months').format('YYYYMM');
            let key = sDate.format('YYYYMM');
            sDate = sDate.add(1, 'months');
            let total = groupList[key];
            let beforeTotal = groupList[beforeKey];
            if (!total) {
                if (!beforeTotal) {
                    beforeTotal = groupList[Object.keys(groupList)[0]];
                }
                groupList[key] = beforeTotal.map(x => {
                    return {date: key, price: x.price, area: x.area, yearMonth: key}
                });
                groupList[key].isEmpty = true;
            }
        }

        groupList = Object.keys(groupList).map(key => {
            return ({
                x: key,
                y: (groupList[key].map(x => x.price * 1).reduce((a, b) => a + b) / groupList[key].length).toFixed(0),
                z: groupList[key].isEmpty ? 0 : groupList[key].length
            })
        });

        let total = {
            pointRadius: 0,
            borderWidth: 2,
            // spanGaps: true,
            borderColor: tradeType === 'rent' ? '#00802E' : '#DDDDDD',
            fill: false,
            label: 'rent',
            data: groupList
        };

        // if (region.type === 'BUILDING' && beforeMonth < 37) {
        //     data = groupBy(result, 'area');
        //     data = Object.keys(data).map(key => {
        //         return {
        //             type: 'scatter',
        //             label: key,
        //             data: data[key].map(x => ({
        //                 x: moment(x.date).format('YYYYMM'),
        //                 y: x.price
        //             }))
        //         }
        //     });
        //     data.unshift(total);
        // } else {
        //     data = [total];
        // }
        data = [total];

        return {labels: result.map(x => moment(x.date)), datasets: data}
    };

    const fetchChart = async () => {
        setIsLoading(true);
        // let tradeList = await getTradeChart(region, region.name, CHART_COLOR_LIST[0]);

        let index = 0;
        let tradeList;
        let labels = [];
        let datasets = [];
        for (const versusRegion of versusRegionList) {
            let versusChart = await getTradeChart(versusRegion, versusRegion.name, CHART_COLOR_LIST[index]);
            labels = labels ? labels : versusChart.labels;
            datasets = datasets.concat(versusChart.datasets);
            index++;
        }

        // let rentList = await getRentChart();

        setChartList({
            labels: labels,
            datasets: datasets,
            // datasets: tradeList.datasets.concat(rentList.datasets),
        });
        setIsLoading(false);
    };

    return (
        <div>
            {
                isLoading ?
                    <div>
                        <Loading isLoading={isLoading}/>
                    </div> :
                    (chartList) ?
                        <>
                            <div>
                                <Line data={chartList}
                                      options={{
                                          onAnimationComplete: function () {
                                              // this.showTooltip(this.datasets[0].points, true);
                                          },
                                          animation: {
                                              duration: 0 // general animation time
                                          },
                                          hover: {
                                              animationDuration: 0 // duration of animations when hovering an item
                                          },
                                          responsiveAnimationDuration: 0, // animation duration after a resize
                                          events: ['mousemove', 'click', 'touchstart', 'touchmove'],
                                          layout: {
                                              padding: {
                                                  top: 28,  //set that fits the best
                                                  right: 15
                                              }
                                          },
                                          tooltips: {
                                              // filter: function (tooltipItem) {
                                              //     return tooltipItem.datasetIndex === 0;
                                              // },
                                              mode: 'index',
                                              caretSize: 0,
                                              position: 'custom',
                                              intersect: false,
                                              displayColors: true,
                                              callbacks: {
                                                  title: function (data) {
                                                      return moment(data[0].label).format('YYYY년 M월');
                                                  },
                                                  label: function (tooltipItem, data) {
                                                      let result = [];
                                                      let dataset = data.datasets[tooltipItem.datasetIndex];
                                                      // console.log(dataset.data[tooltipItem.index]);
                                                      let value = dataset.data[tooltipItem.index];
                                                      let text = '';
                                                      // let date = moment(value.x).format('YYYY년 M월');
                                                      let price = value.y * 1;
                                                      let count = value.z * 1;
                                                      if (count === 0) {
                                                          // return `${dataset.label} ${date} 거래없음`;
                                                          return `${dataset.label} : 거래없음`;
                                                      }
                                                      if (price > 10) {
                                                          text = (price / 10000).toFixed(2) + '억';
                                                      } else {
                                                          text = (price / 100).toFixed(2) + '천';
                                                      }
                                                      return `${dataset.label} : 평균 ${text} (${count}건)`;
                                                  }
                                              }
                                          },
                                          legend: {
                                              display: false
                                          },
                                          scales: {
                                              yAxes: [{
                                                  // position: 'right',
                                                  ticks: {
                                                      // beginAtZero: true,
                                                      stepSize: 20000,

                                                      // Return an empty string to draw the tick line but hide the tick label
                                                      // Return `null` or `undefined` to hide the tick line entirely
                                                      userCallback: function (value, index, values) {
                                                          // Convert the number to a string and splite the string every 3 charaters from the end
                                                          value = value.toString();
                                                          value = value.split(/(?=(?:...)*$)/);

                                                          // Convert the array to a string and format the output
                                                          value = value.join('.');
                                                          if (value > 10) {
                                                              return value / 10 + '억';
                                                          } else {
                                                              return value / 100 + '천';
                                                          }
                                                      }
                                                  }
                                              }],
                                              xAxes: [{
                                                  type: 'time',
                                                  // gridLines: {
                                                  //     lineWidth: 2
                                                  // },
                                                  ticks: {
                                                      autoSkip: false
                                                  },
                                                  time: {
                                                      unit: 'month',
                                                      unitStepSize: 6,
                                                      displayFormats: {
                                                          month: 'YY.MM',
                                                      }
                                                  }
                                              }]
                                          }
                                      }}/>
                            </div>
                            {
                                <div className={cx('flex justify-end items-center font-normal pr-3 pt-3')}>
                                    <span
                                        className={cx('text-black text-sm')}>{period[0].format('YYYY년 M월')} ~ {period[1].format('YYYY년 M월')}</span>
                                    {/*{period[0].format('YYYY년 M월')} ~ {period[1].format('YYYY년 M월')}*/}
                                </div>
                            }
                            <div className={cx('p-5')}>
                                <Range
                                    // marks={beforeYear}
                                    defaultValue={period}
                                    value={period}
                                    min={moment('20060101', 'YYYYMMDD')}
                                    max={moment()}
                                    onChange={(e) => setPeriod([moment(e[0]), moment(e[1])])}
                                    onAfterChange={(e) => {
                                        setStartDate(period[0]);
                                        setEndDate(period[1]);
                                    }}
                                    trackStyle={{
                                        backgroundColor: '#DDDDDD',
                                    }}
                                    railStyle={{
                                        // backgroundColor: '#00802E',
                                    }}
                                />
                            </div>
                        </> :
                        <div>
                            <Result
                                img={<img src={'https://gw.alipayobjects.com/zos/rmsportal/GIyMDJnuqmcqPLpHCSkj.svg'}
                                          style={{width: 40, height: 40}}/>}
                                // title="지역 호가 차트 준비중"
                                message="데이터가 없습니다"
                            />
                        </div>
            }
        </div>
    );
};

export default VersusChart;