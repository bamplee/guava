import React from 'react'

import {Badge, Button, SegmentedControl} from 'antd-mobile';

import classNames from 'classnames/bind';
import styles from './guavaTradeOption.module.scss';
import CaretDownOutlined from '@ant-design/icons/es/icons/CaretDownOutlined';
import {getEndArea, getStartArea} from '../constant';
import {useRecoilState, useRecoilValue} from 'recoil';
import {
    areaTypeState,
    filterAreaState,
    regionState,
    showAreaFilterState,
    showAreaTypeFilterState,
    tradeTypeState
} from '../datatool/state';

const cx = classNames.bind(styles);

const GuavaTradeOption = () => {
    const [areaType, setAreaType] = useRecoilState(areaTypeState);
    const [filterArea, setFilterArea] = useRecoilState(filterAreaState);
    const [showAreaTypeFilter, setShowAreaTypeFilter] = useRecoilState(showAreaTypeFilterState);
    const [showAreaFilter, setShowAreaFilter] = useRecoilState(showAreaFilterState);
    const region = useRecoilValue(regionState);
    const [tradeType, setTradeType] = useRecoilState(tradeTypeState);

    return (
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
                region && region.type === 'BUILDING' ?
                    <Button className={cx('filter_btn', areaType.areaId !== '' ? 'active' : '')}
                            type={areaType.areaId !== '' ? 'primary' : ''}
                            inline
                            onClick={() => setShowAreaTypeFilter(true)}>
                        {
                            areaType.areaId !== '' ?
                                <span>
                                    {areaType.name}
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
    );
};

export default GuavaTradeOption;