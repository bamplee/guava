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
import CloseOutlined from '@ant-design/icons/es/icons/CloseOutlined';

const cx = classNames.bind(styles);

const GuavaTradeOption = ({type}) => {
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
                tintColor={'#00802E'}
                style={{height: 30, width: 140}}
            />
            <div className={cx('filter_area')}>
                <Button className={cx('filter_btn', filterArea[0] === 0 && filterArea[1] === 5 ? '' : 'active')}
                        type={filterArea[0] === 0 && filterArea[1] === 5 ? '' : 'primary'}
                        inline
                        onClick={() => setShowAreaFilter(true)}>
                    {
                        filterArea[0] === 0 && filterArea[1] === 5 ? '면적' : `${getStartArea(filterArea[0])}㎡~${getEndArea(filterArea[1])}㎡`
                    }
                    <CaretDownOutlined/>
                </Button>
                {
                    type === 'BUILDING' &&
                    <Button className={cx('filter_btn', areaType.areaId !== '' ? 'active' : '')}
                            type={areaType.areaId !== '' ? 'primary' : ''}
                            inline
                            onClick={() => areaType.areaId === '' ? setShowAreaTypeFilter(true) : setAreaType({areaId: ''})}>
                        {
                            areaType.areaId !== '' ?
                                <span style={{display: 'flex', alignItems: 'center', justifyContent: 'center'}}>
                                    {areaType.type}<CloseOutlined style={{fontSize: 8, marginLeft: 4}}/>
                                </span> :
                                <span>평형<CaretDownOutlined/></span>
                        }
                    </Button>
                }
            </div>
        </div>
    );
};

export default GuavaTradeOption;