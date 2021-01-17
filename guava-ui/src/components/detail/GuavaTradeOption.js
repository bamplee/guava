import React, {useEffect, useState} from 'react'
import {useHistory, useParams} from 'react-router-dom';

import {Badge, Button, SegmentedControl} from 'antd-mobile';

import classNames from 'classnames/bind';
import styles from './guavaTradeOption.module.scss';
import CaretDownOutlined from '@ant-design/icons/es/icons/CaretDownOutlined';
import {getEndArea, getStartArea, TABLE_OPTION} from '../constant';
import {useRecoilState, useRecoilValue} from 'recoil';
import {
    areaTypeState,
    buildingState,
    filterAreaState,
    regionState,
    showAreaFilterState,
    showAreaTypeFilterState,
    tableOptionState,
    tradeTypeState
} from '../datatool/state';

const cx = classNames.bind(styles);

const GuavaTradeOption = () => {
    const {type} = useParams();
    const history = useHistory();
    const [tabs, setTabs] = useState(0);
    const [areaType, setAreaType] = useRecoilState(areaTypeState);
    // const [region, setRegion] = useRecoilState(regionState);
    const [filterArea, setFilterArea] = useRecoilState(filterAreaState);
    const [showAreaTypeFilter, setShowAreaTypeFilter] = useRecoilState(showAreaTypeFilterState);
    const [showAreaFilter, setShowAreaFilter] = useRecoilState(showAreaFilterState);
    const [tableOption, setTableOption] = useRecoilState(tableOptionState);
    // const [building, setBuilding] = useRecoilState(buildingState);
    const region = useRecoilValue(regionState);
    const building = useRecoilValue(buildingState);
    const [tradeType, setTradeType] = useRecoilState(tradeTypeState);

    useEffect(() => {
        if (tableOption === TABLE_OPTION.TRADE) {
            setTabs(0);
        } else if (tableOption === TABLE_OPTION.MARKET) {
            setTabs(1);
        }
    }, []);

    // const handleTabs = (index) => {
    //     setTabs(index);
    //     if (index === 0) {
    //         setTableOption(TABLE_OPTION.TRADE);
    //     } else if (index === 1) {
    //         setTableOption(TABLE_OPTION.MARKET);
    //     }
    // };

    return (
        <>
            {/*<Tabs tabs={[{title: '매매'}, {title: '호가'}]} initialPage={1} animated={false} useOnPan={false}>*/}
            {/*    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '250px', backgroundColor: '#fff' }}>*/}
            {/*        Content of first tab*/}
            {/*    </div>*/}
            {/*    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '250px', backgroundColor: '#fff' }}>*/}
            {/*        Content of second tab*/}
            {/*    </div>*/}
            {/*    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '250px', backgroundColor: '#fff' }}>*/}
            {/*        Content of third tab*/}
            {/*    </div>*/}
            {/*</Tabs>*/}
            <div className={cx('filter_container')}>
                <SegmentedControl
                    selectedIndex={tradeType === 'trade' ? 0 : 1}
                    onChange={(e) => {
                        let index = e.nativeEvent.selectedSegmentIndex;
                        if (index === 0) {
                            setTradeType('trade');
                        } else {
                            setTradeType('rent');
                        }
                    }}
                    values={[<Badge>매매</Badge>, <Badge>전/월세</Badge>]}
                    tintColor={'#2E92FC'}
                    style={{height: 30, width: 140}}
                />
                {
                    region && region.type === 'BUILDING' && building && building.areaList.length > 0 ?
                        <Button className={cx('filter_btn', areaType.areaId !== '' ? 'active' : '')}
                                type={areaType.areaId !== '' ? 'primary' : ''}
                                inline
                                onClick={() => setShowAreaTypeFilter(true)}>
                            {
                                areaType.areaId !== '' ?
                                    <span>
                                    {Math.ceil(areaType.publicArea * 0.3025) + '평'}
                                </span> :
                                    <span>평형</span>
                            }
                        </Button> :
                        <Button className={cx('filter_btn')}
                                type={filterArea[0] === 0 && filterArea[1] === 5 ? '' : 'primary'}
                                inline
                                onClick={() => setShowAreaFilter(true)}>
                            {
                                filterArea[0] === 0 && filterArea[1] === 5 ? '면적' : `${getStartArea(filterArea[0])}&#13217;~${getEndArea(filterArea[1])}&#13217;`
                            }
                            <CaretDownOutlined/>
                        </Button>
                }
            </div>
        </>
    );
};

export default GuavaTradeOption;