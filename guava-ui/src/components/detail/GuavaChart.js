import React, {useEffect, useState} from 'react'
import {useRecoilState, useRecoilValue} from 'recoil';
import {Badge, Range, SegmentedControl, Slider, Tabs} from 'antd-mobile';

import {
    areaTypeState,
    filterAreaState, regionState,
    tableOptionState,
    tradeDateState
} from '../datatool/state';
import {getChart, getRegionChart} from '../datatool/api';

import classNames from 'classnames/bind';
import styles from './guavaChart.module.scss';
import {Line} from 'react-chartjs-2';
import Chart from 'chart.js';
import moment from 'moment';
import ArrowLeftOutlined from '@ant-design/icons/es/icons/ArrowLeftOutlined';
import {getEndArea, getStartArea, TABLE_OPTION} from '../constant';
import GuavaLoading from './GuavaLoading';

const cx = classNames.bind(styles);

const GuavaChart = () => {
    const [areaType, setAreaType] = useRecoilState(areaTypeState);
    const [tradeDate, setTradeDate] = useRecoilState(tradeDateState);
    const [count, setCount] = useState(0);
    const [chartList, setChartList] = useState([]);
    const [chartTab, setChartTab] = useState(0);
    const [beforeMonth, setBeforeMonth] = useState(36);
    const [sliderValue, setSliderValue] = useState(180 - beforeMonth);
    // const [beforeYear, setBeforeYear] = useState(3);
    const [activeChartIndex, setActiveChartIndex] = useState(null);
    // const [region, setRegion] = useRecoilState(regionState);
    const [filterArea, setFilterArea] = useRecoilState(filterAreaState);
    const [tableOption, setTableOption] = useRecoilState(tableOptionState);
    const region = useRecoilValue(regionState);

    const [period, setPeriod] = useState([moment().subtract(24, 'months'), moment()]);
    const [startDate, setStartDate] = useState(period[0]);
    const [endDate, setEndDate] = useState(period[1]);

    useEffect(() => {
        // fetchChart();
        // initChartEvent();
        initChartEvent();
    }, []);

    useEffect(() => {
        if (region !== null) {
            fetchChart();
        }
    }, [region, startDate, endDate, filterArea, areaType]);

    useEffect(() => {
        if (chartList.datasets && chartList.datasets[0].data.length > 0 && activeChartIndex && activeChartIndex !== -1) {
            setTradeDate(moment(chartList.datasets[0].data[activeChartIndex].x));
            setCount(chartList.datasets[0].data[activeChartIndex].z);
        }
    }, [activeChartIndex]);

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
                ctx.strokeStyle = '#2E92FC';
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

    const fetchChart = async () => {
        let result = [];
        if (region.type === 'BUILDING') {
            result = await getChart(region.buildingId, areaType.areaId, startDate.format('YYYYMM'), endDate.format('YYYYMM'));
        } else {
            let startArea = getStartArea(filterArea[0]);
            let endArea = getEndArea(filterArea[1]);
            result = await getRegionChart(region.id, startArea, endArea, startDate.format('YYYYMM'), endDate.format('YYYYMM'));
        }

        let groupList = groupBy(result.map(x => {
            x.yearMonth = x.date.substring(0, 6);
            return x;
        }), 'yearMonth');

        let sDate = moment(Object.keys(groupList)[0] + '01', 'YYYYMMDD');
        let eDate = moment(Object.keys(groupList)[Object.keys(groupList).length - 1] + '01', 'YYYYMMDD');

        while (sDate.isBefore(eDate)) {
            let beforeKey = moment(sDate).subtract(1, 'months').format('YYYYMM');
            let key = sDate.format('YYYYMM');
            sDate = sDate.add(1, 'months');
            let total = groupList[key];
            let beforeTotal = groupList[beforeKey];
            if (!total) {
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
            // spanGaps: true,
            borderColor: '#2E92FC',
            fill: false,
            label: 'total',
            data: groupList
        };

        let data = [];
        if (region.type === 'BUILDING' && beforeMonth < 37) {
            data = groupBy(result, 'area');
            data = Object.keys(data).map(key => {
                return {
                    type: 'scatter',
                    label: key,
                    data: data[key].map(x => ({
                        x: moment(x.date).format('YYYYMM'),
                        y: x.price
                    }))
                }
            });
            data.unshift(total);
        } else {
            data = [total];
        }

        setChartList({
            labels: result.map(x => moment(x.date)),
            datasets: data,
        });
    };

    return (
        <>
            {
                <div className={cx('chart_container')}>
                    {
                        (chartList && chartList.datasets && chartList.datasets.length > 0) ?
                            <div className={cx('chart')}>
                                <Line data={chartList}
                                      options={{
                                          onAnimationComplete: function () {
                                              this.showTooltip(this.datasets[0].points, true);
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
                                              filter: function (tooltipItem) {
                                                  return tooltipItem.datasetIndex === 0;
                                              },
                                              mode: 'index',
                                              caretSize: 0,
                                              position: 'custom',
                                              intersect: false,
                                              displayColors: false,
                                              callbacks: {
                                                  title: function () {
                                                      return '';
                                                  },
                                                  label: function (tooltipItem, data) {
                                                      let value = data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index];
                                                      let text = '';
                                                      let date = moment(value.x).format('YYYY년 M월');
                                                      let price = value.y * 1;
                                                      let count = value.z * 1;
                                                      if (count === 0) {
                                                          return `${date} 거래없음`;
                                                      }
                                                      if (price > 10) {
                                                          text = (price / 10000).toFixed(2) + '억';
                                                      } else {
                                                          text = (price / 100).toFixed(2) + '천';
                                                      }
                                                      return `${date} 평균 ${text} (${count}건)`;
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
                                                      stepSize: 10000,

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
                            </div> :
                            <GuavaLoading isLoading={true}/>
                    }
                    <div className={cx('slider')}>
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
                                // backgroundColor: '#2E92FC',
                            }}
                        />
                        {/*<Slider*/}
                        {/*    // marks={beforeYear}*/}
                        {/*    defaultValue={sliderValue}*/}
                        {/*    value={sliderValue}*/}
                        {/*    min={0}*/}
                        {/*    max={179}*/}
                        {/*    onChange={(value) => setSliderValue(value)}*/}
                        {/*    onAfterChange={() => setBeforeMonth(180 - sliderValue)}*/}
                        {/*    trackStyle={{*/}
                        {/*        backgroundColor: '#DDDDDD',*/}
                        {/*    }}*/}
                        {/*    railStyle={{*/}
                        {/*        backgroundColor: '#2E92FC',*/}
                        {/*    }}*/}
                        {/*/>*/}
                    </div>
                    <div className={cx('title')}>
                        {period[0].format('YYYY년 M월')} ~ {period[1].format('YYYY년 M월')}
                    </div>
                    {/*{*/}
                    {/*    tradeDate &&*/}
                    {/*    <div className={cx('date')}>*/}
                    {/*        <div className={cx('back_btn')} onClick={() => {*/}
                    {/*            setChartList([]);*/}
                    {/*            setTradeDate(null);*/}
                    {/*            setCount(0);*/}
                    {/*            fetchChart();*/}
                    {/*        }}>*/}
                    {/*            <ArrowLeftOutlined/>*/}
                    {/*        </div>*/}
                    {/*        <span>{tradeDate.format('YYYY년 M월')}</span>*/}
                    {/*        <span className={cx('count')}>({count}건)</span>*/}
                    {/*    </div>*/}
                    {/*}*/}
                </div>
            }
        </>
    );
};

export default GuavaChart;