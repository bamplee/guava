import React, {useEffect, useState} from 'react'
import {useRecoilState, useRecoilValue} from 'recoil';
import {useHistory} from 'react-router-dom';
import Chart from 'chart.js';

import {
    areaTypeState,
    filterAreaState,
    regionState,
    tableOptionState,
    tradeDateState
} from '../datatool/state';
import {getTrade} from '../datatool/api';

import classNames from 'classnames/bind';
import styles from './guavaChart.module.scss';
import {Bar} from 'react-chartjs-2';

const options = {
    legend: {
        display: false
    },
    tooltips: {
        // mode: 'index',
        caretSize: 0,
        position: 'custom',
        // intersect: false,
        displayColors: false,
        callbacks: {
            title: function () {
                return '';
            },
            label: function (tooltipItem, data) {
                if (data.datasets[tooltipItem.datasetIndex]) {
                    // console.log(data.datasets[tooltipItem.datasetIndex]);
                    // console.log(tooltipItem.index);
                    let value = data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index];
                    if (value > 10) {
                        value = (value / 10000).toFixed(2) + '억';
                    } else {
                        value = (value / 100).toFixed(2) + '천';
                    }
                    if (data.datasets[tooltipItem.datasetIndex].list && data.datasets[tooltipItem.datasetIndex].list.length > 0) {
                        let item = data.datasets[tooltipItem.datasetIndex].list[tooltipItem.index];
                        console.log(item);
                        // console.log(value);
                        let minusPrice = item.minusPrice;
                        minusPrice = (minusPrice / 10000).toFixed(2) + '억';

                        return `${item.year}.${item.month}.${item.day} : ${value} (${minusPrice})`;
                    }
                    return `전고가 : ${value}`;
                }
                // let text = '';
                // let date = moment(value.x).format('YYYY년 M월');
                // let price = value.y * 1;
                // let count = value.z * 1;
                // if (count === 0) {
                //     return `${date} 거래없음`;
                // }
                // if (price > 10) {
                //     text = (price / 10000).toFixed(2) + '억';
                // } else {
                //     text = (price / 100).toFixed(2) + '천';
                // }
                return '';
            }
        }
    },
    layout: {
        padding: {
            top: 28,  //set that fits the best
            right: 15
        }
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
        xAxes: [
            {
                ticks: {
                    beginAtZero: true,
                },
            },
        ],
    },
};

const cx = classNames.bind(styles);

const GuavaMarketChart = () => {
    const history = useHistory();
    const [page, setPage] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [isCompleted, setIsCompleted] = useState(false);
    const [chartList, setChartList] = useState({label: '', datasets: []});
    const [areaType, setAreaType] = useRecoilState(areaTypeState);
    const [tradeDate, setTradeDate] = useRecoilState(tradeDateState);
    // const [region, setRegion] = useRecoilState(regionState);
    const [tableOption, setTableOption] = useRecoilState(tableOptionState);
    const [filterArea, setFilterArea] = useRecoilState(filterAreaState);
    const region = useRecoilValue(regionState);

    useEffect(() => {
        // fetchChart();
        // initChartEvent();
        initChartEvent();
    }, []);

    useEffect(() => {
        fetch();
    }, [region, tableOption]);

    const initChartEvent = () => {
        Chart.pluginService.register({afterDraw: chartPlugin});
        Chart.Tooltip.positioners.custom = customTooltip;
    };

    const chartPlugin = (chart, callback) => {
        if (chart.config.type === 'bar' && chart.tooltip._active && chart.tooltip._active.length) {
            if (chart.tooltip._active[0]) {
                const activePoint = chart.tooltip._active[0];
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

                // handleChartClick(activePoint._index);
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

    const fetch = async () => {
        if (!isCompleted && !isLoading) {
            setIsLoading(true);
            let date = '';
            if (tradeDate !== null) {
                date = tradeDate.format('YYYYMM');
            }

            let result = [];
            if (region.type === 'BUILDING') {
                result = await getTrade(tableOption, region.buildingId, page, areaType.areaId, date);
            }

            if (result.length < 100) {
                setIsCompleted(true);
            }

            let groupList = groupBy(result.map(x => {
                x.areaType = x.area.type;
                return x;
            }), 'areaType');

            let keyDataList = [];
            let maxLength = 0;
            for (const key of Object.keys(groupList)) {
                maxLength = maxLength > groupList[key].length ? maxLength : groupList[key].length;
                keyDataList.push(groupList[key]);
            }

            let datasets = [];
            for (let x = 0; x < maxLength; x++) {  // for each row
                let temp = [];
                for (let y = 0; y < keyDataList.length; y++) { // for each clm
                    temp.push(keyDataList[y][x]);
                }
                datasets.push(temp.map(x => !x ? 0 : x));
            }

            let orderDatasets = datasets.map((x, idx) => {
                return {
                    label: idx,
                    list: x,
                    data: x.map(y => y.price * 1),
                    backgroundColor: '#2E92FC',
                }
            });

            let labels = Object.keys(groupList);

            let lineDatasets = Object.keys(groupList).map((key) => [...new Set(groupList[key].map(x => x.beforeMaxPrice * 1))]);
            // let lineDatasets = Object.keys(groupList).map((key) => groupList[key].map(x => x.beforeMaxPrice * 1));

            let realPriceDatasets = {
                type: 'bar',
                label: '전고가',
                data: lineDatasets.flatMap(x => x),
                backgroundColor: '#7C848A',
            };

            orderDatasets.unshift(realPriceDatasets);

            const data = {
                labels: labels,
                datasets: orderDatasets
            };

            setChartList(data);
            setPage(page + 1);
            setIsLoading(false);
        }
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

    return (
        <div className={cx('chart_container')}>
            {/*<Tabs tabs={[*/}
            {/*    // {title: <Badge>최근 1년</Badge>, value: 12},*/}
            {/*    {title: <Badge>최근 3년</Badge>, value: 36},*/}
            {/*    // {title: <Badge>최근 5년</Badge>, value: 60},*/}
            {/*    {title: <Badge>전체</Badge>, value: 240},*/}
            {/*]}*/}
            {/*      onTabClick={(tab, index) => {*/}
            {/*          // setBeforeMonth(tab.value);*/}
            {/*      }}*/}
            {/*      animated={false} useOnPan={false}*/}
            {/*>*/}
            {/*</Tabs>*/}
            <Bar data={chartList} options={options}/>
        </div>
    );
};

export default GuavaMarketChart;